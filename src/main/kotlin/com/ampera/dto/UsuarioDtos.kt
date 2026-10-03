package com.ampera.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Past
import jakarta.validation.constraints.Size
import java.time.LocalDate

data class UsuarioRequest(
    @field:NotBlank @field:Size(max = 120) val nome: String,
    @field:Email @field:NotBlank @field:Size(max = 180) val email: String,
    @field:Size(min = 6, max = 72) val senha: String? = null,
    @field:Size(max = 20) val telefone: String = "",
    @field:Past val dataNascimento: LocalDate? = null,
    val ativo: Boolean = true,
)

data class UsuarioResponse(
    val id: Long,
    val nome: String,
    val email: String,
    val telefone: String,
    val dataNascimento: LocalDate?,
    val ativo: Boolean,
)
