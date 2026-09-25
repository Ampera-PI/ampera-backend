package com.ampera.domain

import kotlin.math.pow

class PrevisorDeConsumo {
    fun preverProximo(consumosDiarios: List<Double>): Double {
        if (consumosDiarios.isEmpty()) return 0.0
        if (consumosDiarios.size == 1) return consumosDiarios.first().coerceAtLeast(0.0)
        val xs = consumosDiarios.indices.map { it.toDouble() }
        val mediaX = xs.average()
        val mediaY = consumosDiarios.average()
        val numerador = xs.zip(consumosDiarios).sumOf { (x, y) -> (x - mediaX) * (y - mediaY) }
        val denominador = xs.sumOf { (it - mediaX).pow(2) }
        val inclinacao = if (denominador == 0.0) 0.0 else numerador / denominador
        val intercepto = mediaY - inclinacao * mediaX
        return (intercepto + inclinacao * consumosDiarios.size).coerceAtLeast(0.0)
    }
}
