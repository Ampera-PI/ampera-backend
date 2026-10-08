package com.ampera.application

import com.ampera.domain.RecursoNaoEncontradoException
import com.ampera.dto.SensorRequest
import com.ampera.dto.SensorResponse
import com.ampera.infrastructure.persistence.Sensor
import com.ampera.infrastructure.persistence.SensorRepository
import com.ampera.mapper.SensorMapper
import org.springframework.stereotype.Service

@Service
class SensorService(
    private val sensores: SensorRepository,
    private val mapper: SensorMapper,
    private val donos: Donos,
    private val sessao: SessaoAtual,
    private val transacao: Transacao,
) {
    suspend fun listar(dispositivoId: Long?, busca: String? = null): List<SensorResponse> = io {
        val usuarioId = sessao.id()
        transacao.ler {
            if (dispositivoId != null) donos.dispositivo(dispositivoId, usuarioId)
            val lista = when {
                !busca.isNullOrBlank() -> sensores.buscarPorModelo(usuarioId, busca.trim())
                    .filter { dispositivoId == null || it.dispositivo.id == dispositivoId }
                dispositivoId == null -> sensores.findByDispositivoComodoResidenciaUsuarioId(usuarioId)
                else -> sensores.findByDispositivoIdAndDispositivoComodoResidenciaUsuarioId(dispositivoId, usuarioId)
            }
            lista.map { mapper.toResponse(it) }
        }
    }

    suspend fun buscar(id: Long): SensorResponse = io {
        val usuarioId = sessao.id()
        transacao.ler { mapper.toResponse(carregar(id, usuarioId)) }
    }

    suspend fun criar(pedido: SensorRequest): SensorResponse = io {
        val usuarioId = sessao.id()
        transacao.escrever {
            val dispositivo = donos.dispositivo(pedido.dispositivoId, usuarioId)
            mapper.toResponse(sensores.save(mapper.toEntity(pedido, dispositivo)))
        }
    }

    suspend fun atualizar(id: Long, pedido: SensorRequest): SensorResponse = io {
        val usuarioId = sessao.id()
        transacao.escrever {
            val sensor = carregar(id, usuarioId)
            mapper.atualizar(sensor, pedido, donos.dispositivo(pedido.dispositivoId, usuarioId))
            mapper.toResponse(sensor)
        }
    }

    suspend fun excluir(id: Long) = io {
        val usuarioId = sessao.id()
        transacao.escrever {
            carregar(id, usuarioId)
            sensores.deleteById(id)
        }
    }

    private fun carregar(id: Long, usuarioId: Long): Sensor {
        val sensor = sensores.findById(id).orElseThrow { RecursoNaoEncontradoException("Sensor não encontrado") }
        donos.dispositivo(sensor.dispositivo.id, usuarioId)
        return sensor
    }
}
