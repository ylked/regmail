package ch.hearc.nde.regmailapi.dto.response

data class LoginResponseDTO(
    val accessToken: TokenResponseDTO,
    val refreshToken: TokenResponseDTO,
    val user: UserResponseDTO,
)
