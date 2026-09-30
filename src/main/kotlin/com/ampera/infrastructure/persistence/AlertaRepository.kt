package com.ampera.infrastructure.persistence

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface AlertaRepository : JpaRepository<Alerta, Long> {
    fun findByDispositivoIdAndAtivoTrue(dispositivoId: Long): List<Alerta>
    fun findByDispositivoComodoResidenciaUsuarioId(usuarioId: Long): List<Alerta>
    fun findByDispositivoIdAndDispositivoComodoResidenciaUsuarioId(dispositivoId: Long, usuarioId: Long): List<Alerta>

    @Query(
        """
        select a from Alerta a
        where a.dispositivo.comodo.residencia.usuario.id = :usuarioId
          and lower(a.mensagem) like lower(concat('%', :termo, '%'))
        order by a.mensagem
        """,
    )
    fun buscarPorMensagem(usuarioId: Long, termo: String): List<Alerta>
}
