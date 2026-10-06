package com.ampera.dto

import com.ampera.domain.Grandeza
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.PositiveOrZero
import jakarta.validation.constraints.Size

data class AlertaRequest(
    @field:NotNull @field:Positive val dispositivoId: Long,
    @field:NotNull val grandeza: Grandeza,
    @field:PositiveOrZero val limiar: Double,
    @field:NotBlank @field:Size(max = 255) val mensagem: String,
    val ativo: Boolean = true,
)

data class AlertaResponse(
    val id: Long,
    val dispositivoId: Long,
    val grandeza: Grandeza,
    val limiar: Double,
    val mensagem: String,
    val ativo: Boolean,
    val disparado: Boolean,
)
