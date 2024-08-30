package ch.hearc.nde.regmailapi.config

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebMvcConfig @Autowired constructor(
    private val authenticatedUserArgumentResolver: AuthenticatedUserArgumentResolver

): WebMvcConfigurer  {
    private val logger: Logger = LoggerFactory.getLogger(WebMvcConfig::class.java)

    override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver>) {
        resolvers.add(authenticatedUserArgumentResolver)
    }

    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**")
            .allowedOrigins("*")
            .allowedMethods("GET", "POST", "PUT", "PATCH" ,"DELETE", "OPTIONS")
            .allowedHeaders("*")
//            .allowCredentials(true)
    }
}