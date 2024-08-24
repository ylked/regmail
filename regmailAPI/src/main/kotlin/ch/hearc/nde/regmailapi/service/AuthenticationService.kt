package ch.hearc.nde.regmailapi.service

import ch.hearc.nde.regmailapi.dto.request.RecoveryRequestDTO
import ch.hearc.nde.regmailapi.dto.request.RefreshRequestDTO
import ch.hearc.nde.regmailapi.dto.response.LoginResponseDTO
import ch.hearc.nde.regmailapi.dto.response.TokenResponseDTO
import ch.hearc.nde.regmailapi.dto.response.UserResponseDTO
import ch.hearc.nde.regmailapi.exception.*
import ch.hearc.nde.regmailapi.model.AccountRecoveryEntity
import ch.hearc.nde.regmailapi.model.EmailVerificationEntity
import ch.hearc.nde.regmailapi.model.RefreshTokenEntity
import ch.hearc.nde.regmailapi.model.UserEntity
import ch.hearc.nde.regmailapi.repository.AccountRecoveryRepository
import ch.hearc.nde.regmailapi.repository.EmailVerificationRepository
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
    private val accountRecoveryRepository: AccountRecoveryRepository,
    private val emailVerificationRepository: EmailVerificationRepository,
    private val jwtService: JwtService,
    private val authenticationManager: AuthenticationManager,
    private val passwordEncoder: PasswordEncoder,
    private val emailService: EmailService,
) {

    companion object {
        private const val EMAIL_REGEX: String = "^[a-zA-Z0-9_!#\$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+\$"
    }

    @Value("\${app.auth.token.emailverification.expiration.ms}")
    private var emailVerificationExpirationMs: Long = 0

    @Value("\${app.auth.token.recovery.expiration.ms}")
    private var recoveryExpirationMs: Long = 0

    @Value("\${app.auth.token.refresh.expiration.ms}")
    private var refreshTokenExpirationMs: Long = 0

    @Value("\${app.auth.token.access.expiration.ms}")
    private var accessTokenExpirationMs: Long = 0

    @Value("\${app.auth.emailverification.min.delay.ms}")
    private var emailVerificationMinDelayMs: Long = 0

    @Value("\${app.auth.recovery.min.delay.ms}")
    private var recoveryMinDelayMs: Long = 0

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

        generateEmailVerificationCodes(user)
        sendVerificationEmail(user)

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
            emailVerified = user.verified,
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

    @Transactional
    fun verifyWithToken(token: String) {
        if (!this.isTokenValid(token)) {
            throw InvalidEmailVerificationToken()
        }

        val user = userRepository.findByEmailVerificationToken(token) ?: throw InvalidEmailVerificationToken()
        revokeEmailVerificationCodes(user)
    }

    @Transactional
    fun verifyWithShortCode(user: UserEntity, shortCode: Long) {
        if (user.emailVerification!!.shortCode != shortCode.toString()) {
            throw InvalidEmailVerificationToken()
        }

        revokeEmailVerificationCodes(user)
    }

    @Transactional
    fun resendVerification(user: UserEntity) {
        if (user.verified) {
            throw AlreadyVerified()
        }

        if (user.emailVerification == null) {
            generateEmailVerificationCodes(user)
            sendVerificationEmail(user)
        } else {
            if (user.emailVerification!!.updatedAt + emailVerificationMinDelayMs > System.currentTimeMillis()) {
                throw TooManyRequests()
            }

            revokeEmailVerificationCodes(user, false)
            generateEmailVerificationCodes(user)
            sendVerificationEmail(user)
        }
    }

    @Transactional
    fun requestRecovery(email: String) {
        val user = userRepository.findByEmail(email) ?: return

        if (user.accountRecovery != null) {
            if (user.accountRecovery!!.expiresAt + recoveryMinDelayMs > System.currentTimeMillis()) {
                return
            } else {
                revokeRecoveryCodes(user)
            }
        }

        generateRecoveryCodes(user)
        sendRecoveryEmail(user)
    }

    @Transactional
    fun recover(dto: RecoveryRequestDTO): LoginResponseDTO {
        if (!isRecoveryTokenValid(dto.token)) {
            throw InvalidRecoveryToken()
        }

        val user = userRepository.findByAccountRecoveryToken(dto.token) ?: throw InvalidRecoveryToken()
        revokeRecoveryCodes(user)

        user._password = hashPassword(dto.password)
        userRepository.save(user)

        return login(user)
    }

    private fun isTokenValid(token: String): Boolean {
        val emailVerification = userRepository.findByEmailVerificationToken(token)?.emailVerification ?: return false
        return emailVerification.expiresAt > System.currentTimeMillis()
    }

    private fun isRecoveryTokenValid(token: String): Boolean {
        val accountRecovery = userRepository.findByAccountRecoveryToken(token)?.accountRecovery ?: return false
        return accountRecovery.expiresAt > System.currentTimeMillis()
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
        val refreshToken = refreshTokenRepository.findByToken(token)
            ?: throw BadCredentialsException("Invalid refresh token")
        if (refreshToken.expiresAt < System.currentTimeMillis()) {
            throw BadCredentialsException("Refresh token expired")
        }
        return refreshToken
    }

    private fun generateEmailVerificationCodes(user: UserEntity) {
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
    }

    private fun revokeEmailVerificationCodes(user: UserEntity, verified: Boolean = true) {
        user.verified = verified
        user.emailVerification = null

        emailVerificationRepository.deleteByUser(user)
        userRepository.save(user)
    }

    private fun generateRecoveryCodes(user: UserEntity) {
        val token: String = TokenGenerator.token()
        val expiresAt: Long = System.currentTimeMillis() + recoveryExpirationMs

        val accountRecovery = AccountRecoveryEntity(
            user = user,
            token = token,
            expiresAt = expiresAt,
        )

        user.accountRecovery = accountRecovery
        userRepository.save(user)
    }

    private fun revokeRecoveryCodes(user: UserEntity) {
        user.accountRecovery = null
        accountRecoveryRepository.deleteByUser(user)
    }

    private fun sendVerificationEmail(user: UserEntity) {
        // TODO: Change this to the user's email
        val dest = "test@test.com"
        val subject = "RegMail - Verify your email address"
        val body = getVerificationCodeBody(user)
        emailService.sendEmail(dest, subject, body)
    }

    private fun sendRecoveryEmail(user: UserEntity) {
        // TODO: Change this to the user's email
        val dest = "test@test.com"
        val subject = "RegMail - Recover your account"
        val body = getRecoveryCodeBody(user)
        emailService.sendEmail(dest, subject, body)
    }

    private fun getRecoveryCodeBody(user: UserEntity): String {
        return "Hello ${user.email},\n\n" +
            "You have requested to recover your account. Please click on the following link to recover your account:\n" +
            "http://localhost:8000/api/auth/recover/${user.accountRecovery?.token}\n\n" +
            "If you did not request this recovery, please ignore this email.\n\n" +
            "Best regards,\n" +
            "The RegMail team"
    }

    private fun getVerificationCodeBody(user: UserEntity): String {
        return "Hello ${user.email},\n\n" +
            "Please verify your email address by clicking on the following link:\n" +
            "http://localhost:8000/api/auth/verify/${user.emailVerification?.token}\n\n" +
            "You can also enter the following code on the website: " +
            "${user.emailVerification?.shortCode}\n\n" +
            "If you did not request this verification, please ignore this email.\n\n" +
            "Best regards,\n" +
            "The RegMail team"
    }

}