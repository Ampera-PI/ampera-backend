package com.ampera.dto

import com.ampera.domain.TipoSensor
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.PastOrPresent
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.PositiveOrZero
import jakarta.validation.constraints.Size
import java.time.LocalDate

data class SensorRequest(
    @field:NotNull @field:Positive val dispositivoId: Long,
    @field:NotNull val tipo: TipoSensor,
    @field:NotBlank @field:Size(max = 20) val unidade: String,
    @field:Size(max = 80) val modelo: String = "",
    @field:PositiveOrZero val precisao: Double = 0.0,
    @field:PastOrPresent val calibradoEm: LocalDate? = null,
    val ativo: Boolean = true,
)

data class SensorResponse(
    val id: Long,
    val dispositivoId: Long,
    val tipo: TipoSensor,
    val unidade: String,
    val modelo: String,
    val precisao: Double,
    val calibradoEm: LocalDate?,
    val ativo: Boolean,
)
