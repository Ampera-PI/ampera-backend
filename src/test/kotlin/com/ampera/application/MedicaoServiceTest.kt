package com.ampera.application

import com.ampera.domain.*
import com.ampera.dto.MedicaoRequest
import com.ampera.infrastructure.persistence.*
import com.ampera.mapper.MedicaoMapper
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.flow.toList
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.data.domain.PageRequest
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.TransactionDefinition
import org.springframework.transaction.support.SimpleTransactionStatus
import java.math.BigDecimal
import java.time.LocalDate
import java.time.ZoneOffset
import java.util.Optional

class MedicaoServiceTest {
    // Repositórios e sessão são mocks; mapper, domínio, posse e Transacao são reais.
    // O stub do transaction manager executa os blocos sem iniciar banco ou Spring.
    private val medicoes = mock(MedicaoRepository::class.java)
    private val tarifas = mock(TarifaRepository::class.java)
    private val alertas = mock(AlertaRepository::class.java)
    private val dispositivos = mock(DispositivoRepository::class.java)
    private val sessao = mock(SessaoAtual::class.java)
    private val tx = mock(PlatformTransactionManager::class.java)
    private val dispositivo = Dispositivo(id = 3, codigoMqtt = "sala").apply {
        comodo.residencia.id = 2
        comodo.residencia.usuario.id = 7
    }
    private val pedido = MedicaoRequest(3, 127.0, 2.0, 254.0, 2.5, " leitura ", false)
    private val service = MedicaoService(
        medicoes, tarifas, alertas, MedicaoMapper(),
        Donos(mock(ResidenciaRepository::class.java), mock(ComodoRepository::class.java), dispositivos),
        sessao, Transacao(tx),
    )

    @BeforeEach
    fun preparar() {
        `when`(sessao.id()).thenReturn(7)
        `when`(tx.getTransaction(any(TransactionDefinition::class.java))).thenReturn(SimpleTransactionStatus())
        `when`(dispositivos.findById(3)).thenReturn(Optional.of(dispositivo))
        `when`(medicoes.save(any(Medicao::class.java))).thenAnswer { it.arguments[0] as Medicao }
    }

    @Test
    fun `criar calcula custo pela primeira tarifa vigente e avalia alertas`() = runBlocking {
        val alto = Alerta(grandeza = Grandeza.POTENCIA, limiar = 200.0)
        val normal = Alerta(grandeza = Grandeza.TENSAO, limiar = 127.0, disparado = true)
        `when`(tarifas.vigentes(2, LocalDate.now(ZoneOffset.UTC))).thenReturn(listOf(
            Tarifa(valorKwh = BigDecimal("0.8")), Tarifa(valorKwh = BigDecimal("9")),
        ))
        `when`(alertas.findByDispositivoIdAndAtivoTrue(3)).thenReturn(listOf(alto, normal))
        val resposta = service.criar(pedido)
        assertEquals(BigDecimal("2.0000"), resposta.custo)
        assertEquals(OrigemMedicao.MANUAL, resposta.origem)
        assertEquals(3L, resposta.dispositivoId)
        assertEquals(127.0, resposta.tensao)
        assertEquals(2.0, resposta.corrente)
        assertEquals(254.0, resposta.potencia)
        assertEquals(2.5, resposta.energiaKwh)
        assertEquals("leitura", resposta.observacao)
        assertFalse(resposta.validada)
        assertTrue(alto.disparado)
        assertFalse(normal.disparado)
        verify(tx).commit(any())
    }

    @Test
    fun `sem tarifa usa valor unitario mesmo apos reinicio do contador`() = runBlocking {
        `when`(medicoes.findTopByDispositivoIdOrderByInstanteDesc(3)).thenReturn(Medicao(id = 9, energiaKwh = 8.0))
        assertEquals(BigDecimal("2.5000"), service.criar(pedido).custo)
    }

    @Test
    fun `dispositivo inexistente impede gravacao`() {
        `when`(dispositivos.findById(3)).thenReturn(Optional.empty())
        assertThrows(RecursoNaoEncontradoException::class.java) { runBlocking { service.criar(pedido) } }
        verifyNoInteractions(medicoes, tarifas, alertas)
        verify(tx).rollback(any())
    }

    @Test
    fun `dispositivo de outro dono impede gravacao`() {
        dispositivo.comodo.residencia.usuario.id = 8
        assertThrows(AcessoNegadoException::class.java) { runBlocking { service.criar(pedido) } }
        verifyNoInteractions(medicoes, tarifas, alertas)
    }

    @Test
    fun `falha de persistencia e propagada sem avaliar alertas`() {
        val falha = IllegalStateException("indisponivel")
        `when`(medicoes.save(any(Medicao::class.java))).thenThrow(falha)
        assertSame(falha, assertThrows(IllegalStateException::class.java) { runBlocking { service.criar(pedido) } })
        verifyNoInteractions(alertas)
        verify(tx).rollback(any())
    }

    @Test
    fun `buscar devolve medicao do dono`() = runBlocking {
        `when`(medicoes.findById(4)).thenReturn(Optional.of(Medicao(id = 4, dispositivo = dispositivo)))
        assertEquals(4L, service.buscar(4).id)
        verify(dispositivos).findById(3)
    }

    @Test
    fun `medicao inexistente impede busca atualizacao e exclusao`() {
        for (operacao in listOf<suspend () -> Any>({ service.buscar(4) }, { service.atualizar(4, pedido) }, { service.excluir(4) })) {
            assertThrows(RecursoNaoEncontradoException::class.java) { runBlocking { operacao() } }
        }
        verify(medicoes, never()).deleteById(anyLong())
        verify(medicoes, never()).save(any(Medicao::class.java))
    }

    @Test
    fun `atualizar preserva identidade e recalcula custo`() = runBlocking {
        val existente = Medicao(id = 4, dispositivo = dispositivo, energiaKwh = 9.0)
        `when`(medicoes.findById(4)).thenReturn(Optional.of(existente))
        `when`(medicoes.findTopByDispositivoIdOrderByInstanteDesc(3)).thenReturn(existente)
        val resposta = service.atualizar(4, pedido)
        assertEquals(4L, resposta.id)
        assertEquals(BigDecimal("2.5000"), resposta.custo)
        assertEquals("leitura", existente.observacao)
        verify(medicoes).save(existente)
    }

    @Test
    fun `excluir verifica posse antes de remover`() = runBlocking {
        `when`(medicoes.findById(4)).thenReturn(Optional.of(Medicao(id = 4, dispositivo = dispositivo)))
        service.excluir(4)
        val ordem = inOrder(dispositivos, medicoes)
        ordem.verify(dispositivos).findById(3)
        ordem.verify(medicoes).deleteById(4)
    }

    @Test
    fun `outro dono nao pode buscar atualizar ou excluir medicao`() {
        dispositivo.comodo.residencia.usuario.id = 8
        `when`(medicoes.findById(4)).thenReturn(Optional.of(Medicao(id = 4, dispositivo = dispositivo)))
        for (operacao in listOf<suspend () -> Any>({ service.buscar(4) }, { service.atualizar(4, pedido) }, { service.excluir(4) })) {
            assertThrows(AcessoNegadoException::class.java) { runBlocking { operacao() } }
        }
        verify(medicoes, never()).deleteById(anyLong())
        verify(medicoes, never()).save(any(Medicao::class.java))
    }

    @Test
    fun `recentes sem filtro limita pagina ao minimo`() = runBlocking {
        val pagina = PageRequest.of(0, 1)
        `when`(medicoes.findByDispositivoComodoResidenciaUsuarioIdOrderByInstanteDesc(7, pagina))
            .thenReturn(listOf(Medicao(id = 4, dispositivo = dispositivo)))
        assertEquals(listOf(4L), service.recentes(null, 0).toList().map { it.id })
        verifyNoInteractions(dispositivos)
    }

    @Test
    fun `recentes por dispositivo limita pagina ao maximo e ignora busca branca`() = runBlocking {
        assertTrue(service.recentes(3, 999, " ").toList().isEmpty())
        verify(medicoes).findByDispositivoIdOrderByInstanteDesc(3, PageRequest.of(0, 200))
        verify(dispositivos).findById(3)
    }

    @Test
    fun `busca textual remove espacos e filtra dispositivo`() = runBlocking {
        `when`(medicoes.buscarPorObservacao(7, "leitura", PageRequest.of(0, 10))).thenReturn(listOf(
            Medicao(id = 4, dispositivo = dispositivo), Medicao(id = 5, dispositivo = Dispositivo(id = 99)),
        ))
        assertEquals(listOf(4L), service.recentes(3, 10, " leitura ").toList().map { it.id })
        assertEquals(listOf(4L, 5L), service.recentes(null, 10, "leitura").toList().map { it.id })
    }

    @Test
    fun `recentes nega dispositivo alheio antes da consulta`() {
        dispositivo.comodo.residencia.usuario.id = 8
        assertThrows(AcessoNegadoException::class.java) { runBlocking { service.recentes(3, 10) } }
        verifyNoInteractions(medicoes)
    }
}
