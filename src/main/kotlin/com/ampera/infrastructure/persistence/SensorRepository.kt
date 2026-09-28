package com.ampera.infrastructure.persistence

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface SensorRepository : JpaRepository<Sensor, Long> {
    fun findByDispositivoComodoResidenciaUsuarioId(usuarioId: Long): List<Sensor>
    fun findByDispositivoIdAndDispositivoComodoResidenciaUsuarioId(dispositivoId: Long, usuarioId: Long): List<Sensor>

    @Query(
        """
        select s from Sensor s
        where s.dispositivo.comodo.residencia.usuario.id = :usuarioId
          and lower(s.modelo) like lower(concat('%', :termo, '%'))
        order by s.modelo
        """,
    )
    fun buscarPorModelo(usuarioId: Long, termo: String): List<Sensor>
}
