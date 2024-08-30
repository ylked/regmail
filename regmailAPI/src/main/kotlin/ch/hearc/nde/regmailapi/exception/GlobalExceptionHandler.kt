package ch.hearc.nde.regmailapi.exception

import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.security.SignatureException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.security.authentication.AccountStatusException
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.lang.Exception

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception, request: HttpServletRequest, response: HttpServletResponse): ResponseEntity<Any> {
        val status: HttpStatus
        val message: String?

        when (e) {
            is IncorrectUsernameOrPassword -> {
                status = HttpStatus.UNAUTHORIZED
                message = e.message
            }

            is EmailAlreadyTaken -> {
                status = HttpStatus.CONFLICT
                message = e.message
            }

            is InvalidEmailFormat -> {
                status = HttpStatus.BAD_REQUEST
                message = e.message
            }

            is InvalidEmailVerificationToken -> {
                status = HttpStatus.BAD_REQUEST
                message = e.message
            }

            is TooManyRequests -> {
                status = HttpStatus.TOO_MANY_REQUESTS
                message = e.message
            }

            is AlreadyVerified -> {
                status = HttpStatus.FORBIDDEN
                message = e.message
            }

            is InvalidRecoveryToken -> {
                status = HttpStatus.UNAUTHORIZED
                message = e.message
            }

            is BadCredentialsException -> {
                status = HttpStatus.UNAUTHORIZED
                message = e.message
            }

            is AccountStatusException -> {
                status = HttpStatus.FORBIDDEN
                message = e.message
            }

            is AccessDeniedException -> {
                status = HttpStatus.FORBIDDEN
                message = e.message
            }

            is SignatureException -> {
                status = HttpStatus.FORBIDDEN
                message = e.message
            }

            is ExpiredJwtException -> {
                status = HttpStatus.FORBIDDEN
                message = e.message
            }

            is MalformedJwtException -> {
                status = HttpStatus.BAD_REQUEST
                message = e.message
            }

            is HttpMessageNotReadableException -> {
                status = HttpStatus.BAD_REQUEST
                message = "Request body is missing"
            }

            else -> {
                status = HttpStatus.INTERNAL_SERVER_ERROR
                message = e.message
            }
        }

        response.setHeader("Access-Control-Allow-Origin", "*")
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, PATCH, DELETE, OPTIONS")
        response.setHeader("Access-Control-Allow-Headers", "*")
        response.setHeader("Access-Control-Allow-Credentials", "true")

        val body = mapOf("status" to status.value(), "message" to message)
        return ResponseEntity.status(status).body(body)
    }
}