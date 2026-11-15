package com.ampera.domain

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class PrevisorDeConsumoTest {
    private val previsor = PrevisorDeConsumo()

    @Test
    fun `serie vazia retorna zero`() {
        assertEquals(0.0, previsor.preverProximo(emptyList()))
    }

    @Test
    fun `um dia retorna o proprio consumo`() {
        assertEquals(2.5, previsor.preverProximo(listOf(2.5)))
    }

    @Test
    fun `um dia com valor negativo limita previsao a zero`() {
        assertEquals(0.0, previsor.preverProximo(listOf(-2.5)))
    }

    @Test
    fun `consumo constante mantem previsao`() {
        assertEquals(3.0, previsor.preverProximo(listOf(3.0, 3.0, 3.0)), 0.000001)
    }

    @Test
    fun `tendencia decrescente nunca produz previsao negativa`() {
        assertEquals(0.0, previsor.preverProximo(listOf(3.0, 1.0)), 0.000001)
    }

    @Test
    fun `regressao de serie nao linear considera todos os pontos`() {
        // x = [0,1,2], y = [2,4,3]: inclinação 0,5; intercepto 2,5; próximo = 4.
        assertEquals(4.0, previsor.preverProximo(listOf(2.0, 4.0, 3.0)), 0.000001)
    }
}
