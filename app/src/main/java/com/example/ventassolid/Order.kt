package com.example.ventassolid

import java.time.LocalDateTime

data class Order(
    val id: String,
    val customer: Customer,
    val items: List<CartItem>,
    val date: LocalDateTime,
    val subtotal: Double,
    val tax: Double,
    val total: Double
)
