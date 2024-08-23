package ch.hearc.nde.regmailapi.dto.response

data class TokenResponseDTO(
    val token: String,
    val validityMs: Long,
    val expiresAt: Long,
)
