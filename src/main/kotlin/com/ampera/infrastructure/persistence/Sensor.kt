package com.ampera.infrastructure.persistence

import com.ampera.domain.TipoSensor
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
import java.time.LocalDate

@Entity
@Table(name = "sensor")
class Sensor(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "dispositivo_id")
    var dispositivo: Dispositivo = Dispositivo(),
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    var tipo: TipoSensor = TipoSensor.TENSAO,
    @Column(nullable = false, length = 20)
    var unidade: String = "",
    @Column(nullable = false, length = 80)
    var modelo: String = "",
    @Column(nullable = false)
    var precisao: Double = 0.0,
    @Column(name = "calibrado_em")
    var calibradoEm: LocalDate? = null,
    @Column(nullable = false)
    var ativo: Boolean = true,
)
