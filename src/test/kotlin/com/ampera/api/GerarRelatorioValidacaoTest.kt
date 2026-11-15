package com.ampera.api

import com.ampera.dto.GerarRelatorioRequest
import jakarta.validation.Validation
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.LocalDate

class GerarRelatorioValidacaoTest {
    // Validação real de entrada: as anotações do DTO são aplicadas pela API, não pelo serviço.
    private val dia = LocalDate.of(2026, 11, 1)

    private fun campos(pedido: GerarRelatorioRequest): Set<String> =
        Validation.buildDefaultValidatorFactory().use { factory ->
            factory.validator.validate(pedido).map { it.propertyPath.toString() }.toSet()
        }

    @Test
    fun `id um e titulo de 120 caracteres sao aceitos`() {
        assertEquals(emptySet<String>(), campos(GerarRelatorioRequest(1, dia, dia, "x".repeat(120))))
    }

    @Test
    fun `id zero ou negativo e recusado`() {
        for (id in listOf(0L, -1L)) {
            assertEquals(setOf("residenciaId"), campos(GerarRelatorioRequest(id, dia, dia)))
        }
    }

    @Test
    fun `titulo de 121 caracteres e recusado`() {
        assertEquals(setOf("titulo"), campos(GerarRelatorioRequest(1, dia, dia, "x".repeat(121))))
    }

    @Test
    fun `titulo vazio e permitido para aplicar padrao no servico`() {
        assertEquals(emptySet<String>(), campos(GerarRelatorioRequest(1, dia, dia, "")))
    }
}
