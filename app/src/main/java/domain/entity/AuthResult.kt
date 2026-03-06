package domain.entity

data class AuthResult(
    val uid: String,
    val token: String,
    val email: String
)