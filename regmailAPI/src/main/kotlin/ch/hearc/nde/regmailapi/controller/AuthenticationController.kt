package ch.hearc.nde.regmailapi.controller

import ch.hearc.nde.regmailapi.config.AuthenticatedUser
import ch.hearc.nde.regmailapi.dto.request.RefreshRequestDTO
import ch.hearc.nde.regmailapi.dto.request.RegisterRequestDTO
import ch.hearc.nde.regmailapi.dto.response.LoginResponseDTO
import ch.hearc.nde.regmailapi.dto.response.SimpleResponseDTO
import ch.hearc.nde.regmailapi.exception.EmailAlreadyTaken
import ch.hearc.nde.regmailapi.exception.InvalidEmailFormat
import ch.hearc.nde.regmailapi.model.UserEntity
import ch.hearc.nde.regmailapi.service.AuthenticationService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
class AuthenticationController @Autowired constructor(
    private val service: AuthenticationService,
) {

    @PostMapping("/register")
    fun register(
        @RequestBody dto: RegisterRequestDTO,
    ): ResponseEntity<LoginResponseDTO> {
        val response = service.register(dto.email, dto.password)
        return ResponseEntity.ok(response)
    }

    @PostMapping("/login")
    fun login(
        @RequestBody dto: RegisterRequestDTO,
    ): ResponseEntity<LoginResponseDTO> {
        val response = service.login(dto.email, dto.password)
        return ResponseEntity.ok(response)
    }

    @PostMapping("/verify/{token}")
    fun verify(
        @PathVariable token: String,
    ): ResponseEntity<String> {
        service.verifyWithToken(token)
        return ResponseEntity.ok("Email verified")
    }

    @PostMapping("/logout")
    fun logout(
        @AuthenticatedUser user: UserEntity,
    ): ResponseEntity<SimpleResponseDTO> {
        service.logout(user)
        return ResponseEntity.ok(SimpleResponseDTO("User logged out", true))
    }

    @PostMapping("/refresh")
    fun refresh(
        @RequestBody dto: RefreshRequestDTO,
    ): ResponseEntity<LoginResponseDTO> {
        val response = service.refresh(dto)
        return ResponseEntity.ok(response)
    }
}