package com.example.ventassolid

class Cart(
    val customer: Customer
) {
    private val _items = mutableListOf<CartItem>()
    val items: List<CartItem> get() = _items

    fun addItem(product: Product, quantity: Int) {
        _items.add(CartItem(product, quantity))
    }
}