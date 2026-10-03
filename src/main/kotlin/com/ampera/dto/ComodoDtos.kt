package com.ampera.dto

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.PositiveOrZero
import jakarta.validation.constraints.Size

data class ComodoRequest(
    @field:NotNull @field:Positive val residenciaId: Long,
    @field:NotBlank @field:Size(max = 120) val nome: String,
    @field:NotBlank @field:Size(max = 40) val tipo: String = "Outro",
    @field:Min(-5) @field:Max(200) val andar: Int = 0,
    @field:PositiveOrZero val areaM2: Double = 0.0,
    val ativo: Boolean = true,
)

data class ComodoResponse(
    val id: Long,
    val residenciaId: Long,
    val nome: String,
    val tipo: String,
    val andar: Int,
    val areaM2: Double,
    val ativo: Boolean,
)
