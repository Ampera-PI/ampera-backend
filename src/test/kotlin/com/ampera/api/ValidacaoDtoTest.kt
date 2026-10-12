package com.ampera.api

import com.ampera.dto.AlertaRequest
import com.ampera.dto.ComodoRequest
import com.ampera.dto.ResidenciaRequest
import com.ampera.dto.TarifaRequest
import com.ampera.dto.UsuarioRequest
import com.ampera.domain.Grandeza
import jakarta.validation.Validation
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDate

class ValidacaoDtoTest {
    private val validador = Validation.buildDefaultValidatorFactory().validator

    private fun campos(dto: Any) = validador.validate(dto).map { it.propertyPath.toString() }.toSet()

    @Test
    fun `residencia valida passa sem violacoes`() {
        val dto = ResidenciaRequest("Casa", "Rua A, 10", "Santos", 80.0, 3)
        assertTrue(validador.validate(dto).isEmpty())
    }

    @Test
    fun `residencia recusa nome em branco e moradores fora do limite`() {
        val dto = ResidenciaRequest(" ", "Rua A, 10", "Santos", -1.0, 0)
        assertEquals(setOf("nome", "areaM2", "numeroMoradores"), campos(dto))
    }

    @Test
    fun `usuario recusa email invalido senha curta e nascimento futuro`() {
        val dto = UsuarioRequest("Ana", "nao-e-email", "123", dataNascimento = LocalDate.now().plusDays(1))
        assertEquals(setOf("email", "senha", "dataNascimento"), campos(dto))
    }

    @Test
    fun `tarifa exige valor maior que zero`() {
        val dto = TarifaRequest(1, valorKwh = BigDecimal.ZERO, vigenciaInicio = LocalDate.now())
        assertEquals(setOf("valorKwh"), campos(dto))
    }

    @Test
    fun `comodo recusa andar fora do limite e nome longo demais`() {
        val dto = ComodoRequest(1, "x".repeat(121), "Sala", 500, 10.0)
        assertEquals(setOf("nome", "andar"), campos(dto))
    }

    @Test
    fun `alerta recusa mensagem vazia e limiar negativo`() {
        val dto = AlertaRequest(1, Grandeza.POTENCIA, -5.0, "")
        assertEquals(setOf("limiar", "mensagem"), campos(dto))
    }
}
