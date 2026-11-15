package com.ampera.application

import com.ampera.domain.AcessoNegadoException
import com.ampera.domain.RecursoNaoEncontradoException
import com.ampera.domain.RegraDeNegocioException
import com.ampera.dto.GerarRelatorioRequest
import com.ampera.infrastructure.persistence.*
import com.ampera.mapper.RelatorioMapper
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.TransactionDefinition
import org.springframework.transaction.support.SimpleTransactionStatus
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate
import java.util.Optional

class RelatorioServiceTest {
    // Mocks isolam persistência e sessão; não há contexto Spring nem banco de dados.
    // Donos, Transacao, mapper, consumo, custo e previsão executam suas regras reais.
    private val relatorios = mock(RelatorioRepository::class.java)
    private val medicoes = mock(MedicaoRepository::class.java)
    private val tarifas = mock(TarifaRepository::class.java)
    private val residencias = mock(ResidenciaRepository::class.java)
    private val sessao = mock(SessaoAtual::class.java)
    private val tx = mock(PlatformTransactionManager::class.java)
    private val casa = Residencia(id = 2, usuario = Usuario(id = 7))
    private val inicio = LocalDate.of(2026, 11, 1)
    private val fim = inicio.plusDays(1)
    private val pedido = GerarRelatorioRequest(2, inicio, fim, " Consumo da casa ")
    private val de = Instant.parse("2026-11-01T00:00:00Z")
    private val ate = Instant.parse("2026-11-03T00:00:00Z")
    private val service = RelatorioService(
        relatorios, medicoes, tarifas, RelatorioMapper(),
        Donos(residencias, mock(ComodoRepository::class.java), mock(DispositivoRepository::class.java)),
        sessao, Transacao(tx),
    )

    @BeforeEach
    fun preparar() {
        `when`(sessao.id()).thenReturn(7)
        `when`(residencias.findById(2)).thenReturn(Optional.of(casa))
        // Stub do transaction manager permite executar o bloco; não comprova atomicidade real.
        `when`(tx.getTransaction(any(TransactionDefinition::class.java))).thenReturn(SimpleTransactionStatus())
        // Stub de save simula a atribuição de uma identidade pela persistência.
        `when`(relatorios.save(any(Relatorio::class.java))).thenAnswer {
            (it.arguments[0] as Relatorio).apply { id = 42 }
        }
    }

    private fun leitura(instante: String, energia: Double) =
        Medicao(instante = Instant.parse(instante), energiaKwh = energia)

    private fun doisDias() = listOf(
        leitura("2026-11-01T00:00:00Z", 10.0),
        leitura("2026-11-01T23:59:59Z", 12.0),
        leitura("2026-11-02T00:00:00Z", 12.0),
        leitura("2026-11-02T23:59:59Z", 16.0),
    )

    @Test
    fun `gerar calcula consumo custo previsao e persiste todos os campos`(): Unit = runBlocking {
        `when`(medicoes.doPeriodo(2, de, ate)).thenReturn(doisDias())
        `when`(tarifas.vigentes(2, fim)).thenReturn(listOf(
            Tarifa(valorKwh = BigDecimal("0.75")), Tarifa(valorKwh = BigDecimal("9")),
        ))
        val antes = Instant.now()
        val resposta = service.gerar(pedido)
        val depois = Instant.now()
        assertEquals(42L, resposta.id)
        assertEquals(2L, resposta.residenciaId)
        assertEquals("Consumo da casa", resposta.titulo)
        assertEquals(inicio, resposta.inicio)
        assertEquals(fim, resposta.fim)
        assertEquals(6.0, resposta.consumoKwh, 0.000001)
        assertEquals(BigDecimal("4.5000"), resposta.custoTotal)
        assertEquals(6.0, resposta.previsaoKwh, 0.000001)
        assertFalse(resposta.criadoEm.isBefore(antes))
        assertFalse(resposta.criadoEm.isAfter(depois))
        verify(medicoes).doPeriodo(2, de, ate)
        verify(tarifas).vigentes(2, fim)
        verify(relatorios, times(1)).save(any(Relatorio::class.java))
        verify(tx).commit(any())
    }

    @Test
    fun `sem tarifa vigente calcula custo com valor unitario`(): Unit = runBlocking {
        `when`(medicoes.doPeriodo(2, de, ate)).thenReturn(doisDias())
        `when`(tarifas.vigentes(2, fim)).thenReturn(emptyList())
        assertEquals(BigDecimal("6.0000"), service.gerar(pedido).custoTotal)
    }

    @Test
    fun `sem medicoes ainda persiste relatorio com consumo custo e previsao zero`(): Unit = runBlocking {
        `when`(medicoes.doPeriodo(2, de, ate)).thenReturn(emptyList())
        val resposta = service.gerar(pedido)
        assertEquals(0.0, resposta.consumoKwh)
        assertEquals(BigDecimal("0.0000"), resposta.custoTotal)
        assertEquals(0.0, resposta.previsaoKwh)
        verify(relatorios).save(any(Relatorio::class.java))
    }

    @Test
    fun `titulo em branco recebe titulo padrao`(): Unit = runBlocking {
        assertEquals("Relatório gerado", service.gerar(pedido.copy(titulo = "   ")).titulo)
        assertEquals("Relatório gerado", service.gerar(pedido.copy(titulo = "")).titulo)
    }

    @Test
    fun `periodo de um unico dia e valido e previsao repete consumo diario`(): Unit = runBlocking {
        val fimExclusivo = Instant.parse("2026-11-02T00:00:00Z")
        `when`(medicoes.doPeriodo(2, de, fimExclusivo)).thenReturn(doisDias().take(2))
        val resposta = service.gerar(pedido.copy(fim = inicio))
        assertEquals(2.0, resposta.consumoKwh)
        assertEquals(2.0, resposta.previsaoKwh)
        verify(medicoes).doPeriodo(2, de, fimExclusivo)
    }

    @Test
    fun `reinicio do contador soma leitura atual e deltas seguintes`(): Unit = runBlocking {
        `when`(medicoes.doPeriodo(2, de, ate)).thenReturn(listOf(
            leitura("2026-11-01T01:00:00Z", 10.0),
            leitura("2026-11-01T02:00:00Z", 12.0),
            leitura("2026-11-01T03:00:00Z", 1.0),
            leitura("2026-11-01T04:00:00Z", 3.0),
        ))
        assertEquals(5.0, service.gerar(pedido).consumoKwh)
    }

    @Test
    fun `agrupar em UTC nao soma diferenca entre dias`(): Unit = runBlocking {
        `when`(medicoes.doPeriodo(2, de, ate)).thenReturn(listOf(
            leitura("2026-11-01T23:59:59Z", 10.0),
            leitura("2026-11-02T00:00:00Z", 30.0),
            leitura("2026-11-02T01:00:00Z", 34.0),
        ))
        assertEquals(4.0, service.gerar(pedido).consumoKwh)
    }

    @Test
    fun `fim anterior ao inicio impede consultas e persistencia`() {
        assertThrows(RegraDeNegocioException::class.java) {
            runBlocking { service.gerar(pedido.copy(fim = inicio.minusDays(1))) }
        }
        verifyNoInteractions(residencias, medicoes, tarifas, relatorios)
        verify(tx).rollback(any())
    }

    @Test
    fun `residencia inexistente impede calculo e persistencia`() {
        `when`(residencias.findById(2)).thenReturn(Optional.empty())
        assertThrows(RecursoNaoEncontradoException::class.java) { runBlocking { service.gerar(pedido) } }
        verifyNoInteractions(medicoes, tarifas, relatorios)
    }

    @Test
    fun `residencia de outro dono impede calculo e persistencia`() {
        casa.usuario.id = 8
        assertThrows(AcessoNegadoException::class.java) { runBlocking { service.gerar(pedido) } }
        verifyNoInteractions(medicoes, tarifas, relatorios)
    }

    @Test
    fun `sessao ausente impede iniciar geracao`() {
        `when`(sessao.id()).thenThrow(AcessoNegadoException("Não autenticado"))
        assertThrows(AcessoNegadoException::class.java) { runBlocking { service.gerar(pedido) } }
        verifyNoInteractions(tx, residencias, medicoes, tarifas, relatorios)
    }

    @Test
    fun `falha ao consultar medicoes e propagada sem salvar`() {
        val falha = IllegalStateException("consulta indisponível")
        `when`(medicoes.doPeriodo(2, de, ate)).thenThrow(falha)
        // A recuperação de stack trace das corrotinas pode copiar a exceção.
        val recebida = assertThrows(IllegalStateException::class.java) { runBlocking { service.gerar(pedido) } }
        assertEquals(falha.message, recebida.message)
        verifyNoInteractions(tarifas, relatorios)
        verify(tx).rollback(any())
    }

    @Test
    fun `falha ao salvar e propagada e nao confirma transacao`() {
        val falha = IllegalStateException("persistência indisponível")
        `when`(relatorios.save(any(Relatorio::class.java))).thenThrow(falha)
        val recebida = assertThrows(IllegalStateException::class.java) { runBlocking { service.gerar(pedido) } }
        assertEquals(falha.message, recebida.message)
        verify(tx).rollback(any())
        verify(tx, never()).commit(any())
    }
}
