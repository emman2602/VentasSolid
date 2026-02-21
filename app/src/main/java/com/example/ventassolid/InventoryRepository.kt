package com.example.ventassolid

interface InventoryRepository {
    fun getStock(product: Product): Int
    fun reduceStock(product: Product, quantity: Int)
    fun addStock(product: Product, quantity: Int)
}