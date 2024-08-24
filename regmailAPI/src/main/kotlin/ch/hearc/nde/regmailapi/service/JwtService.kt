package ch.hearc.nde.regmailapi.service

import ch.hearc.nde.regmailapi.model.RefreshTokenEntity
import ch.hearc.nde.regmailapi.model.UserEntity
import ch.hearc.nde.regmailapi.tools.TokenGenerator
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.nio.charset.StandardCharsets
import java.security.Key
import java.util.*


@Service
class JwtService {

    @Value("\${app.auth.token.secret}")
    private lateinit var secret: String

    @Value("\${app.auth.token.access.expiration.ms}")
    private var jwtExpiration: Long = 0

    @Value("\${app.auth.token.refresh.expiration.ms}")
    private var jwtRefreshExpiration: Long = 0

    private fun buildToken(
        extra: Map<String, Any>,
        user: UserDetails,
    ): String {
        return Jwts.builder()
            .setClaims(extra)
            .setSubject(user.username)
            .setExpiration(Date(System.currentTimeMillis() + jwtExpiration))
            .signWith(getSigningKey())
            .compact()
    }

    private fun getSigningKey(): Key {
        val keyBytes: ByteArray = secret.toByteArray(StandardCharsets.UTF_8)
        return Keys.hmacShaKeyFor(keyBytes)
    }


    private fun extractClaims(token: String): Claims {
        return Jwts.parserBuilder()
            .setSigningKey(getSigningKey())
            .build()
            .parseClaimsJws(token)
            .body
    }

    fun extractUsername(token: String): String {
        return extractClaims(token).subject
    }

    private fun extractExpiration(token: String): Date {
        return extractClaims(token).expiration
    }

    private fun isTokenExpired(token: String): Boolean {
        return extractExpiration(token).before(Date())
    }

    public fun generateAccessToken(user: UserDetails): String {
        return buildToken(mapOf(), user)
    }

    public fun isTokenValid(token: String, user: UserDetails): Boolean {
        return user.username == extractUsername(token) && !isTokenExpired(token)
    }

    public fun isTokenValid(token: String): Boolean {
        return !isTokenExpired(token)
    }

    fun generateRefreshToken(
        user: UserEntity,
        expiration: Long = System.currentTimeMillis() + jwtRefreshExpiration
    ): RefreshTokenEntity {
        val token: String = TokenGenerator.token()
        val tokenEntity = RefreshTokenEntity(
            token = token,
            expiresAt = expiration,
            user = user,
        )

        return tokenEntity
    }
}