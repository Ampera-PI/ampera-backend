package com.ampera.application

import com.ampera.domain.AcessoNegadoException
import com.ampera.domain.RecursoNaoEncontradoException
import com.ampera.infrastructure.persistence.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.mockito.Mockito.*
import java.util.Optional

class DonosTest {
    // Apenas o acesso aos dados é simulado; a cadeia de posse e a regra são reais.
    private val residencias = mock(ResidenciaRepository::class.java)
    private val comodos = mock(ComodoRepository::class.java)
    private val dispositivos = mock(DispositivoRepository::class.java)
    private val donos = Donos(residencias, comodos, dispositivos)
    private val casa = Residencia(id = 2, usuario = Usuario(id = 7))
    private val sala = Comodo(id = 3, residencia = casa)
    private val dispositivo = Dispositivo(id = 4, comodo = sala)

    @Test
    fun `dono acessa residencia comodo e dispositivo`() {
        `when`(residencias.findById(2)).thenReturn(Optional.of(casa))
        `when`(comodos.findById(3)).thenReturn(Optional.of(sala))
        `when`(dispositivos.findById(4)).thenReturn(Optional.of(dispositivo))
        assertSame(casa, donos.residencia(2, 7))
        assertSame(sala, donos.comodo(3, 7))
        assertSame(dispositivo, donos.dispositivo(4, 7))
    }

    @ParameterizedTest
    @ValueSource(strings = ["residencia", "comodo", "dispositivo"])
    fun `recurso ausente gera excecao de nao encontrado`(tipo: String) {
        assertThrows(RecursoNaoEncontradoException::class.java) { acessar(tipo, 7) }
    }

    @ParameterizedTest
    @ValueSource(strings = ["residencia", "comodo", "dispositivo"])
    fun `recurso existente de outro dono gera acesso negado`(tipo: String) {
        `when`(residencias.findById(2)).thenReturn(Optional.of(casa))
        `when`(comodos.findById(3)).thenReturn(Optional.of(sala))
        `when`(dispositivos.findById(4)).thenReturn(Optional.of(dispositivo))
        assertThrows(AcessoNegadoException::class.java) { acessar(tipo, 8) }
    }

    private fun acessar(tipo: String, usuario: Long): Any = when (tipo) {
        "residencia" -> donos.residencia(2, usuario)
        "comodo" -> donos.comodo(3, usuario)
        else -> donos.dispositivo(4, usuario)
    }
}
