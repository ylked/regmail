package ch.hearc.nde.regmailapi.config

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebMvcConfig @Autowired constructor(
    private val authenticatedUserArgumentResolver: AuthenticatedUserArgumentResolver
): WebMvcConfigurer  {
    override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver>) {
        resolvers.add(authenticatedUserArgumentResolver)
    }
}