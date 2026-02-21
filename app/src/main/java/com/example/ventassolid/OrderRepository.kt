package com.example.ventassolid

interface OrderRepository {
    fun saveOrder(order: Order)
    fun getCustomerHistory(customer: Customer): List<Order>
}