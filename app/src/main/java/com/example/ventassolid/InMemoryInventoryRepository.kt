package com.example.ventassolid

class InMemoryInventoryRepository: InventoryRepository {
    private val stockMap = mutableMapOf<String, Int>()
    override fun getStock(product: Product): Int = stockMap[product.id] ?: 0

    override fun reduceStock(product: Product, quantity: Int) {
       val currentStock = getStock(product)
        if (currentStock < quantity){
            throw Exception("Insufficient stock for ${product.name}")
        }
        stockMap[product.id] = currentStock - quantity
    }

    override fun addStock(product: Product, quantity: Int) {
        stockMap[product.id] = getStock(product) + quantity
    }
}