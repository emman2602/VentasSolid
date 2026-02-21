package com.example.ventassolid

import java.time.LocalDateTime
import java.util.UUID

class OrderProcessor(
    private val inventoryRepository: InventoryRepository,
    private val orderRepository: OrderRepository,
    private val taxCalculator: TaxCalculator
) {
    fun checkout(cart: Cart): Order {
        if (cart.items.isEmpty()) throw Exception("Cannot process an empty cart")

        cart.items.forEach { item ->
            if (inventoryRepository.getStock(item.product) < item.quantity){
                throw Exception("Not enough stock for '${item.product.name}'")
            }
        }

        val subtotal = cart.items.sumOf { it.product.price * it.quantity }
        val tax = taxCalculator.calculate(subtotal)
        val total = subtotal+tax

        val order = Order(
            UUID.randomUUID().toString(),
            cart.customer,
            cart.items.toList(),
            LocalDateTime.now(),
            subtotal,
            tax,
            total
        )

        cart.items.forEach { inventoryRepository.reduceStock(it.product, it.quantity) }
        orderRepository.saveOrder(order)
        return order
    }
}