package ch.hearc.nde.regmailapi.config

import ch.hearc.nde.regmailapi.model.UserEntity
import ch.hearc.nde.regmailapi.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.MethodParameter
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

@Component
class AuthenticatedUserArgumentResolver @Autowired constructor(
    private val repository: UserRepository
): HandlerMethodArgumentResolver {


    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return parameter.hasParameterAnnotation(AuthenticatedUser::class.java)
    }

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?
    ): Any? {
        val authentication = SecurityContextHolder.getContext().authentication
        val email = authentication?.principal as? String ?: throw Exception("No authenticated user")
        return repository.findByEmail(email) ?: throw Exception("User not found")
    }
}