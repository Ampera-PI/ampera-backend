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
import java.time.LocalDate

@Entity
@Table(name = "dispositivo")
class Dispositivo(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "comodo_id")
    var comodo: Comodo = Comodo(),
    @Column(nullable = false, length = 120)
    var nome: String = "",
    @Column(name = "codigo_mqtt", nullable = false, unique = true, length = 80)
    var codigoMqtt: String = "",
    @Column(nullable = false, length = 80)
    var fabricante: String = "",
    @Column(name = "potencia_nominal_w", nullable = false)
    var potenciaNominalW: Double = 0.0,
    var instalacao: LocalDate? = null,
    @Column(nullable = false)
    var ativo: Boolean = true,
)
