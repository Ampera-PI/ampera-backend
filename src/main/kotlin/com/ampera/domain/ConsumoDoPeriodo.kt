package com.ampera.domain

class ConsumoDoPeriodo {
    fun diario(leituras: List<LeituraEnergia>): List<Double> {
        return leituras
            .groupBy { it.dia }
            .toSortedMap()
            .values
            .map { dia -> deltas(dia.sortedBy { it.instanteMillis }.map { it.energiaKwh }) }
    }

    fun deltas(energias: List<Double>): Double {
        var total = 0.0
        var anterior: Double? = null
        energias.forEach { atual ->
            if (anterior != null) {
                total += if (atual < anterior!!) atual else atual - anterior!!
            }
            anterior = atual
        }
        return total
    }
}
