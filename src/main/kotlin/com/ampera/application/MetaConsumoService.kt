package com.ampera.application

import com.ampera.domain.ConsumoDoPeriodo
import com.ampera.domain.RecursoNaoEncontradoException
import com.ampera.domain.RegraDeNegocioException
import com.ampera.dto.MetaConsumoRequest
import com.ampera.dto.MetaConsumoResponse
import com.ampera.infrastructure.persistence.MedicaoRepository
import com.ampera.infrastructure.persistence.MetaConsumo
import com.ampera.infrastructure.persistence.MetaConsumoRepository
import com.ampera.mapper.MetaConsumoMapper
import org.springframework.stereotype.Service

@Service
class MetaConsumoService(
    private val metas: MetaConsumoRepository,
    private val medicoes: MedicaoRepository,
    private val mapper: MetaConsumoMapper,
    private val donos: Donos,
    private val sessao: SessaoAtual,
    private val transacao: Transacao,
    private val consumo: ConsumoDoPeriodo = ConsumoDoPeriodo(),
) {
    suspend fun listar(residenciaId: Long?, busca: String? = null): List<MetaConsumoResponse> = io {
        val usuarioId = sessao.id()
        transacao.ler {
            if (residenciaId != null) donos.residencia(residenciaId, usuarioId)
            val lista = when {
                !busca.isNullOrBlank() -> metas.buscarPorTitulo(usuarioId, busca.trim())
                    .filter { residenciaId == null || it.residencia.id == residenciaId }
                residenciaId == null -> metas.findByResidenciaUsuarioId(usuarioId)
                else -> metas.findByResidenciaIdAndResidenciaUsuarioId(residenciaId, usuarioId)
            }
            lista.map { resposta(it) }
        }
    }

    suspend fun buscar(id: Long): MetaConsumoResponse = io {
        val usuarioId = sessao.id()
        transacao.ler { resposta(carregar(id, usuarioId)) }
    }

    suspend fun criar(pedido: MetaConsumoRequest): MetaConsumoResponse = io {
        val usuarioId = sessao.id()
        transacao.escrever {
            validar(pedido)
            val residencia = donos.residencia(pedido.residenciaId, usuarioId)
            resposta(metas.save(mapper.toEntity(pedido, residencia)))
        }
    }

    suspend fun atualizar(id: Long, pedido: MetaConsumoRequest): MetaConsumoResponse = io {
        val usuarioId = sessao.id()
        transacao.escrever {
            validar(pedido)
            val meta = carregar(id, usuarioId)
            mapper.atualizar(meta, pedido, donos.residencia(pedido.residenciaId, usuarioId))
            resposta(meta)
        }
    }

    suspend fun excluir(id: Long) = io {
        val usuarioId = sessao.id()
        transacao.escrever {
            carregar(id, usuarioId)
            metas.deleteById(id)
        }
    }

    private fun resposta(meta: MetaConsumo): MetaConsumoResponse {
        val leituras = medicoes.doPeriodo(meta.residencia.id, inicioDoDia(meta.inicio), inicioDoDia(meta.fim.plusDays(1)))
        return mapper.toResponse(meta, consumo.diario(leituras.map { it.paraLeituraEnergia() }).sum())
    }

    private fun validar(pedido: MetaConsumoRequest) {
        if (pedido.fim.isBefore(pedido.inicio)) throw RegraDeNegocioException("Fim da meta anterior ao início")
    }

    private fun carregar(id: Long, usuarioId: Long): MetaConsumo {
        val meta = metas.findById(id).orElseThrow { RecursoNaoEncontradoException("Meta não encontrada") }
        donos.residencia(meta.residencia.id, usuarioId)
        return meta
    }
}
