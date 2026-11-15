package com.ampera.domain

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ConsumoDoPeriodoTest {
    private val consumo = ConsumoDoPeriodo()

    @Test
    fun `sem leituras nao ha dias nem consumo`() {
        assertEquals(emptyList<Double>(), consumo.diario(emptyList()))
        assertEquals(0.0, consumo.deltas(emptyList()))
    }

    @Test
    fun `primeira leitura e apenas linha de base`() {
        assertEquals(0.0, consumo.deltas(listOf(100.0)))
    }

    @Test
    fun `leituras iguais nao acrescentam consumo`() {
        assertEquals(0.0, consumo.deltas(listOf(10.0, 10.0, 10.0)))
    }

    @Test
    fun `diario ordena dias e instantes antes de calcular`() {
        val resultado = consumo.diario(listOf(
            LeituraEnergia("2026-11-02", 4, 25.0),
            LeituraEnergia("2026-11-01", 2, 12.0),
            LeituraEnergia("2026-11-02", 3, 20.0),
            LeituraEnergia("2026-11-01", 1, 10.0),
        ))
        assertEquals(listOf(2.0, 5.0), resultado)
    }

    @Test
    fun `cada dia possui linha de base independente sem preencher dias ausentes`() {
        val resultado = consumo.diario(listOf(
            LeituraEnergia("2026-11-01", 1, 10.0),
            LeituraEnergia("2026-11-03", 3, 100.0),
            LeituraEnergia("2026-11-03", 4, 103.0),
        ))
        assertEquals(listOf(0.0, 3.0), resultado)
    }
}
