package com.example.ventassolid

class StoreViewer(
    private val orderRepository: OrderRepository
) {
    fun showCustomerHistory(customer: Customer){
        println("\n--- Purchase History for ${customer.name} ---")
        val history = orderRepository.getCustomerHistory(customer)

        if (history.isEmpty()){
            println("No purchases found")
            return
        }

        history.forEach { order ->
            println("Order ID: ${order.id}---Date: ${order.date.toLocalDate()}")
            order.items.forEach { item ->
                println("  - ${item.quantity}x ${item.product.name} ($${item.product.price} ea)")
            }
            println("  Subtotal: $${"%.2f".format(order.subtotal)} | Tax: $${"%.2f".format(order.tax)} | Total: $${"%.2f".format(order.total)}\n")
        }
    }
}