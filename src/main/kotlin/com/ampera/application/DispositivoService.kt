package com.ampera.application

import com.ampera.domain.RegraDeNegocioException
import com.ampera.dto.DispositivoRequest
import com.ampera.dto.DispositivoResponse
import com.ampera.infrastructure.persistence.DispositivoRepository
import com.ampera.mapper.DispositivoMapper
import org.springframework.stereotype.Service

@Service
class DispositivoService(
    private val dispositivos: DispositivoRepository,
    private val mapper: DispositivoMapper,
    private val donos: Donos,
    private val sessao: SessaoAtual,
    private val transacao: Transacao,
) {
    suspend fun listar(comodoId: Long?, busca: String? = null): List<DispositivoResponse> = io {
        val usuarioId = sessao.id()
        transacao.ler {
            if (comodoId != null) donos.comodo(comodoId, usuarioId)
            val lista = when {
                !busca.isNullOrBlank() -> dispositivos.buscarPorNome(usuarioId, busca.trim())
                    .filter { comodoId == null || it.comodo.id == comodoId }
                comodoId == null -> dispositivos.findByComodoResidenciaUsuarioId(usuarioId)
                else -> dispositivos.findByComodoIdAndComodoResidenciaUsuarioId(comodoId, usuarioId)
            }
            lista.map { mapper.toResponse(it) }
        }
    }

    suspend fun buscar(id: Long): DispositivoResponse = io {
        val usuarioId = sessao.id()
        transacao.ler { mapper.toResponse(donos.dispositivo(id, usuarioId)) }
    }

    suspend fun criar(pedido: DispositivoRequest): DispositivoResponse = io {
        val usuarioId = sessao.id()
        transacao.escrever {
            if (dispositivos.findByCodigoMqtt(pedido.codigoMqtt.trim()) != null) {
                throw RegraDeNegocioException("Código MQTT já usado")
            }
            val comodo = donos.comodo(pedido.comodoId, usuarioId)
            mapper.toResponse(dispositivos.save(mapper.toEntity(pedido, comodo)))
        }
    }

    suspend fun atualizar(id: Long, pedido: DispositivoRequest): DispositivoResponse = io {
        val usuarioId = sessao.id()
        transacao.escrever {
            val dispositivo = donos.dispositivo(id, usuarioId)
            dispositivos.findByCodigoMqtt(pedido.codigoMqtt.trim())?.let {
                if (it.id != id) throw RegraDeNegocioException("Código MQTT já usado")
            }
            mapper.atualizar(dispositivo, pedido, donos.comodo(pedido.comodoId, usuarioId))
            mapper.toResponse(dispositivo)
        }
    }

    suspend fun excluir(id: Long) = io {
        val usuarioId = sessao.id()
        transacao.escrever {
            donos.dispositivo(id, usuarioId)
            dispositivos.deleteById(id)
        }
    }
}
