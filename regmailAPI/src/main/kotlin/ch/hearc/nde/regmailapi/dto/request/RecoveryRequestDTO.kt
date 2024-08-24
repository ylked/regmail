package ch.hearc.nde.regmailapi.dto.request

data class RecoveryRequestDTO(
    val password: String,
    val token: String,
)