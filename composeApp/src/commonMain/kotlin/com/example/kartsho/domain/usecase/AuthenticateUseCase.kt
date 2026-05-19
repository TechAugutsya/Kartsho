package com.example.kartsho.domain.usecase

import com.example.kartsho.domain.model.AuthMode
import com.example.kartsho.domain.model.User
import com.example.kartsho.domain.model.UserRole
import com.example.kartsho.domain.repository.IKartshoRepository

class AuthenticateUseCase(private val repository: IKartshoRepository) {
    suspend operator fun invoke(
        mode: AuthMode,
        role: UserRole,
        name: String,
        email: String,
        password: String
    ): Pair<User?, String?> {
        return repository.authenticate(mode, role, name, email, password)
    }
}
