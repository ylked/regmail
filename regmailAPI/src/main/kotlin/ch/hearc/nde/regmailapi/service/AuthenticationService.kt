package ch.hearc.nde.regmailapi.service

import ch.hearc.nde.regmailapi.dto.request.RefreshRequestDTO
import ch.hearc.nde.regmailapi.dto.response.LoginResponseDTO
import ch.hearc.nde.regmailapi.dto.response.TokenResponseDTO
import ch.hearc.nde.regmailapi.dto.response.UserResponseDTO
import ch.hearc.nde.regmailapi.exception.EmailAlreadyTaken
import ch.hearc.nde.regmailapi.exception.InvalidEmailFormat
import ch.hearc.nde.regmailapi.exception.InvalidEmailVerificationToken
import ch.hearc.nde.regmailapi.model.EmailVerificationEntity
import ch.hearc.nde.regmailapi.model.RefreshTokenEntity
import ch.hearc.nde.regmailapi.model.UserEntity
import ch.hearc.nde.regmailapi.repository.RefreshTokenRepository
import ch.hearc.nde.regmailapi.repository.UserRepository
import ch.hearc.nde.regmailapi.tools.TokenGenerator
import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthenticationService @Autowired constructor(
    private val userRepository: UserRepository,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val jwtService: JwtService,
    private val authenticationManager: AuthenticationManager,
    private val passwordEncoder: PasswordEncoder,
) {

    companion object {
        private const val EMAIL_REGEX: String = "^[a-zA-Z0-9_!#\$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+\$"
    }

    @Value("\${app.auth.token.emailverification.expirationms}")
    private var emailVerificationExpirationMs: Long = 0

    @Value("\${app.auth.token.refresh.expirationms}")
    private var refreshTokenExpirationMs: Long = 0

    @Value("\${app.auth.token.access.expirationms}")
    private var accessTokenExpirationMs: Long = 0

    @Transactional
    fun register(
        email: String,
        password: String,
    ): LoginResponseDTO {
        if (!this.isEmailUnique(email)) {
            throw EmailAlreadyTaken()
        }

        if (!this.isEmailValid(email)) {
            throw InvalidEmailFormat()
        }

        val user = UserEntity(
            email = email,
            _password = hashPassword(password),
        )

        val token: String = TokenGenerator.token()
        val shortCode: String = TokenGenerator.shortCode()
        val expiresAt: Long = System.currentTimeMillis() + emailVerificationExpirationMs

        val emailVerification = EmailVerificationEntity(
            user = user,
            token = token,
            shortCode = shortCode,
            expiresAt = expiresAt,
        )

        user.emailVerification = emailVerification

        userRepository.save(user)

        return login(user)
    }

    @Transactional
    fun login(
        email: String,
        password: String,
    ): LoginResponseDTO {

        authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(email, password)
        )

        val user = userRepository.findByEmail(email) ?: throw BadCredentialsException("Bad credentials")
        return login(user)
    }

    private fun login(
        user: UserEntity,
        expiration: Long = System.currentTimeMillis() + accessTokenExpirationMs,
    ): LoginResponseDTO {
        val jwt: String = jwtService.generateAccessToken(user)
        val refreshToken: RefreshTokenEntity = jwtService.generateRefreshToken(user, expiration)

        user.refreshToken.addLast(refreshToken)
        userRepository.save(user)

        val accessTokenDTO = TokenResponseDTO(
            token = jwt,
            validityMs = accessTokenExpirationMs,
            expiresAt = System.currentTimeMillis() + accessTokenExpirationMs,
        )

        val refreshTokenDTO = TokenResponseDTO(
            token = refreshToken.token,
            validityMs = refreshTokenExpirationMs,
            expiresAt = refreshToken.expiresAt,
        )

        val userDTO = UserResponseDTO(
            id = user.id,
            email = user.email,
            emailVerified = user.emailVerification?.verified ?: false,
        )

        return LoginResponseDTO(
            accessToken = accessTokenDTO,
            refreshToken = refreshTokenDTO,
            user = userDTO,
        )
    }

    @Transactional
    fun refresh(
        dto: RefreshRequestDTO,
    ): LoginResponseDTO {
        val entity = findRefreshTokenAndCheckValidity(dto.refreshToken)
        val user = entity.user!!
        revokeRefreshToken(user)
        return login(
            user,
            entity.expiresAt
        )
    }

    @Transactional
    fun logout(
        user: UserEntity
    ) {
        revokeRefreshToken(user)
    }

    fun verifyWithToken(token: String) {
        if(!this.isTokenValid(token)){
            throw InvalidEmailVerificationToken()
        }

        val user = userRepository.findByEmailVerificationToken(token) ?: throw InvalidEmailVerificationToken()

        user.emailVerification?.verified = true
        user.emailVerification?.token = null
        user.emailVerification?.shortCode = null
        user.emailVerification?.expiresAt = 0

        userRepository.save(user)
    }

    fun verifyWithShortCode(user: UserEntity, shortCode: Long) {
        if(user.emailVerification!!.shortCode != shortCode.toString()){
            throw InvalidEmailVerificationToken()
        }

        user.emailVerification?.verified = true
        user.emailVerification?.token = null
        user.emailVerification?.shortCode = null
        user.emailVerification?.expiresAt = 0

        userRepository.save(user)
    }

    private fun isTokenValid(token: String): Boolean {
        val emailVerification = userRepository.findByEmailVerificationToken(token)?.emailVerification ?: return false
        return emailVerification.expiresAt > System.currentTimeMillis()
    }

    private fun isEmailUnique(email: String): Boolean {
        return userRepository.findByEmail(email) == null
    }

    private fun isEmailValid(email: String): Boolean {
        return EMAIL_REGEX.toRegex().matches(email)
    }

    private fun hashPassword(password: String): String {
        return passwordEncoder.encode(password)
    }

    private fun revokeRefreshToken(user: UserEntity) {
        refreshTokenRepository.deleteByUser(user)
    }

    private fun findRefreshTokenAndCheckValidity(token: String): RefreshTokenEntity {
        val refreshToken = refreshTokenRepository.findByToken(token) ?: throw BadCredentialsException("Invalid refresh token")
        if (refreshToken.expiresAt < System.currentTimeMillis()) {
            throw BadCredentialsException("Refresh token expired")
        }
        return refreshToken
    }

}