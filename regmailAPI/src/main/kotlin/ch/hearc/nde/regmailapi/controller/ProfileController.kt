package ch.hearc.nde.regmailapi.controller

import ch.hearc.nde.regmailapi.config.AuthenticatedUser
import ch.hearc.nde.regmailapi.dto.response.SimpleResponseDTO
import ch.hearc.nde.regmailapi.model.UserEntity
import ch.hearc.nde.regmailapi.service.AuthenticationService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/profile")
class ProfileController @Autowired constructor(
    private val authenticationService: AuthenticationService,
) {
    @PostMapping("/verify/{shortCode}")
    fun verify(
        @PathVariable shortCode: Long,
        @AuthenticatedUser user: UserEntity,
    ): ResponseEntity<SimpleResponseDTO> {
        authenticationService.verifyWithShortCode(user, shortCode)
        return ResponseEntity.ok(SimpleResponseDTO("Email successfully verified", true))
    }
}