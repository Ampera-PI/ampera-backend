package com.ampera.infrastructure.persistence

import com.ampera.domain.OrigemMedicao
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.math.BigDecimal
import java.time.Instant

@Entity
@Table(name = "medicao")
class Medicao(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "dispositivo_id")
    var dispositivo: Dispositivo = Dispositivo(),
    var tensao: Double = 0.0,
    var corrente: Double = 0.0,
    var potencia: Double = 0.0,
    @Column(name = "energia_kwh")
    var energiaKwh: Double = 0.0,
    var custo: BigDecimal = BigDecimal.ZERO,
    var instante: Instant = Instant.EPOCH,
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    var origem: OrigemMedicao = OrigemMedicao.MANUAL,
    @Column(nullable = false, length = 200)
    var observacao: String = "",
    @Column(nullable = false)
    var validada: Boolean = true,
)
