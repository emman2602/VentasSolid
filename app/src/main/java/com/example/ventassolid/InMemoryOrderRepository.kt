package com.example.ventassolid

class InMemoryOrderRepository: OrderRepository {
    private val orders = mutableListOf<Order>()
    override fun saveOrder(order: Order) {
        orders.add(order)
    }

    override fun getCustomerHistory(customer: Customer): List<Order> {
        return orders.filter { it.customer.id == customer.id }
    }
}