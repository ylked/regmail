package ch.hearc.nde.regmailapi.controller

import ch.hearc.nde.regmailapi.config.AuthenticatedUser
import ch.hearc.nde.regmailapi.model.UserEntity
import ch.hearc.nde.regmailapi.service.AuthenticationService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class Test @Autowired constructor(
    private val authService: AuthenticationService,
) {
    @GetMapping("/api/test")
    fun test(
        @AuthenticatedUser user: UserEntity
    ): ResponseEntity<String> {
        return ResponseEntity.ok("Hello ${user.email}")
    }
}