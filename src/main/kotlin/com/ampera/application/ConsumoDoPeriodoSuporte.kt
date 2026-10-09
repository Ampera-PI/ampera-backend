package com.ampera.application

import com.ampera.domain.LeituraEnergia
import com.ampera.infrastructure.persistence.Medicao
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

internal fun inicioDoDia(dia: LocalDate): Instant = dia.atStartOfDay().toInstant(ZoneOffset.UTC)

internal fun Medicao.paraLeituraEnergia() =
    LeituraEnergia(instante.atZone(ZoneOffset.UTC).toLocalDate().toString(), instante.toEpochMilli(), energiaKwh)
