package com.ampera.domain

class LinhaDeBaseEnergia {
    fun reiniciou(anterior: Double?, atual: Double): Boolean = anterior != null && atual < anterior
}
