package com.ampera.infrastructure.persistence

import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.time.Instant

interface MedicaoRepository : JpaRepository<Medicao, Long> {
    fun findByDispositivoIdOrderByInstanteDesc(dispositivoId: Long, pageable: Pageable): List<Medicao>
    fun findTopByDispositivoIdOrderByInstanteDesc(dispositivoId: Long): Medicao?
    fun findByDispositivoComodoResidenciaUsuarioIdOrderByInstanteDesc(usuarioId: Long, pageable: Pageable): List<Medicao>

    @Query(
        """
        select m from Medicao m
        where m.dispositivo.comodo.residencia.usuario.id = :usuarioId
          and lower(m.observacao) like lower(concat('%', :termo, '%'))
        order by m.instante desc
        """,
    )
    fun buscarPorObservacao(usuarioId: Long, termo: String, pageable: Pageable): List<Medicao>

    @Query(
        """
        select m from Medicao m
        where m.dispositivo.comodo.residencia.id = :residenciaId
          and m.instante >= :inicio
          and m.instante < :fim
        order by m.instante asc
        """,
    )
    fun doPeriodo(residenciaId: Long, inicio: Instant, fim: Instant): List<Medicao>
}
