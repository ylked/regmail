package ch.hearc.nde.regmailapi.dto.response

data class UserResponseDTO(
    val id: Long,
    val email: String,
    val emailVerified: Boolean,
)
