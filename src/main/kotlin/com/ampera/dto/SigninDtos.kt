package com.ampera.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class SigninRequest(
    @field:Email @field:NotBlank val email: String,
    @field:NotBlank @field:Size(max = 72) val senha: String,
)

data class SigninResponse(val token: String, val usuario: UsuarioResponse)
