package com.example.kartsho.domain.model

enum class UserRole(val label: String) {
    Buyer("Buyer"),
    Supplier("Supplier"),
    Admin("Admin")
}

enum class AuthMode {
    Login,
    SignUp
}

data class User(
    val id: String,
    val name: String,
    val email: String,
    val password: String,
    val role: UserRole
)
