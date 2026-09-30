package com.ampera.infrastructure.persistence

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface DispositivoRepository : JpaRepository<Dispositivo, Long> {
    fun findByCodigoMqtt(codigoMqtt: String): Dispositivo?
    fun findByComodoResidenciaUsuarioId(usuarioId: Long): List<Dispositivo>
    fun findByComodoIdAndComodoResidenciaUsuarioId(comodoId: Long, usuarioId: Long): List<Dispositivo>

    @Query(
        """
        select d from Dispositivo d
        where d.comodo.residencia.usuario.id = :usuarioId
          and lower(d.nome) like lower(concat('%', :termo, '%'))
        order by d.nome
        """,
    )
    fun buscarPorNome(usuarioId: Long, termo: String): List<Dispositivo>
}
