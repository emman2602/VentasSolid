package com.example.ventassolid

class StandardTaxCalculator(
    private val taxRate: Double = 0.16
): TaxCalculator{
    override fun calculate(subtotal: Double): Double {
        return subtotal*taxRate
    }
}