package ch.hearc.nde.regmailapi.filter

import ch.hearc.nde.regmailapi.exception.InvalidEmailVerificationToken
import ch.hearc.nde.regmailapi.service.JwtService
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.servlet.HandlerExceptionResolver


@Component
class JwtAuthenticationFilter(
    private val handlerExceptionResolver: HandlerExceptionResolver,
    private val userDetailsService: UserDetailsService,
    private val jwtService: JwtService,
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authHeader: String? = request.getHeader("Authorization")

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response)
            return
        }

        try {
            val token: String = authHeader.substring(7)
            val email: String = jwtService.extractUsername(token)

            val authentication: Authentication? = SecurityContextHolder.getContext().authentication

            if (authentication == null) {
                if (jwtService.isTokenValid(token)) {
                    val authToken = UsernamePasswordAuthenticationToken(
                        email,
                        null,
                        mutableListOf()
                    )

                    authToken.details = WebAuthenticationDetailsSource().buildDetails(request)
                    SecurityContextHolder.getContext().authentication = authToken
                } else {
                    throw InvalidEmailVerificationToken()
                }
            }

            filterChain.doFilter(request, response)
        } catch (e: Exception) {
            handlerExceptionResolver.resolveException(request, response, null, e);
        }
    }
}