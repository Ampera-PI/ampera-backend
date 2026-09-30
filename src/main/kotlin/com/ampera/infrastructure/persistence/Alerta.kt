package com.ampera.infrastructure.persistence

import com.ampera.domain.Grandeza
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

@Entity
@Table(name = "alerta")
class Alerta(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "dispositivo_id")
    var dispositivo: Dispositivo = Dispositivo(),
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    var grandeza: Grandeza = Grandeza.POTENCIA,
    var limiar: Double = 0.0,
    @Column(nullable = false, length = 255)
    var mensagem: String = "",
    var ativo: Boolean = true,
    var disparado: Boolean = false,
)
