package com.ampera.infrastructure.persistence

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate

@Entity
@Table(name = "relatorio")
class Relatorio(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "residencia_id")
    var residencia: Residencia = Residencia(),
    @Column(nullable = false, length = 120)
    var titulo: String = "Relatório",
    var inicio: LocalDate = LocalDate.EPOCH,
    var fim: LocalDate = LocalDate.EPOCH,
    @Column(name = "consumo_kwh")
    var consumoKwh: Double = 0.0,
    @Column(name = "custo_total")
    var custoTotal: BigDecimal = BigDecimal.ZERO,
    @Column(name = "previsao_kwh")
    var previsaoKwh: Double = 0.0,
    @Column(name = "criado_em")
    var criadoEm: Instant = Instant.EPOCH,
)
