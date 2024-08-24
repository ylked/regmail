package ch.hearc.nde.regmailapi.exception

import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.security.SignatureException
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.security.authentication.AccountStatusException
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.lang.Exception

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception): ProblemDetail {
        var errorDetails: ProblemDetail? = null

        when(e) {
            is IncorrectUsernameOrPassword -> {
                errorDetails = ProblemDetail.forStatusAndDetail(
                    HttpStatus.UNAUTHORIZED,
                    e.message
                )
            }
            is EmailAlreadyTaken -> {
                errorDetails = ProblemDetail.forStatusAndDetail(
                    HttpStatus.CONFLICT,
                    e.message
                )
            }
            is InvalidEmailFormat -> {
                errorDetails = ProblemDetail.forStatusAndDetail(
                    HttpStatus.BAD_REQUEST,
                    e.message
                )
            }
            is InvalidEmailVerificationToken -> {
                errorDetails = ProblemDetail.forStatusAndDetail(
                    HttpStatus.BAD_REQUEST,
                    e.message
                )
            }
            is TooManyRequests -> {
                errorDetails = ProblemDetail.forStatusAndDetail(
                    HttpStatus.TOO_MANY_REQUESTS,
                    e.message
                )
            }
            is AlreadyVerified -> {
                errorDetails = ProblemDetail.forStatusAndDetail(
                    HttpStatus.FORBIDDEN,
                    e.message
                )
            }
            is InvalidRecoveryToken -> {
                errorDetails = ProblemDetail.forStatusAndDetail(
                    HttpStatus.UNAUTHORIZED,
                    e.message
                )
            }
            is BadCredentialsException -> {
                errorDetails = ProblemDetail.forStatusAndDetail(
                    HttpStatus.UNAUTHORIZED,
                    e.message
                )
            }
            is AccountStatusException -> {
                errorDetails = ProblemDetail.forStatusAndDetail(
                    HttpStatus.FORBIDDEN,
                    e.message
                )
            }
            is AccessDeniedException -> {
                errorDetails = ProblemDetail.forStatusAndDetail(
                    HttpStatus.FORBIDDEN,
                    e.message
                )
            }
            is SignatureException -> {
                errorDetails = ProblemDetail.forStatusAndDetail(
                    HttpStatus.FORBIDDEN,
                    e.message
                )
            }
            is ExpiredJwtException -> {
                errorDetails = ProblemDetail.forStatusAndDetail(
                    HttpStatus.FORBIDDEN,
                    e.message
                )
            }
            is MalformedJwtException -> {
                errorDetails = ProblemDetail.forStatusAndDetail(
                    HttpStatus.BAD_REQUEST,
                    e.message
                )
            }
            else -> {
                errorDetails = ProblemDetail.forStatusAndDetail(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    e.message
                )
            }
        }

        return errorDetails
    }
}