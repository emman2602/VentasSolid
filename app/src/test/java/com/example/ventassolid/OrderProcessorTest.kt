package com.example.ventassolid

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class OrderProcessorTest {

    private lateinit var orderProcessor: OrderProcessor
    private lateinit var inventoryRepo: FakeInventoryRepository
    private lateinit var orderRepo: FakeOrderRepository
    private lateinit var taxCalculator: TaxCalculator

    @Before
    fun setup() {
        inventoryRepo = FakeInventoryRepository()
        orderRepo = FakeOrderRepository()

        // Stub for TaxCalculator: Applies a 10% tax for testing purposes
        taxCalculator = object : TaxCalculator {
            override fun calculate(amount: Double): Double {
                return amount * 0.10
            }
        }

        orderProcessor = OrderProcessor(inventoryRepo, orderRepo, taxCalculator)
    }

    @Test
    fun `should throw exception when stock is not enough before confirming order`() {
        // Arrange
        val customer = Customer("C1", "John Doe", "john@example.com")
        val product = Product("P1", "Smartphone", 500.0)

        // We set only 1 item in stock
        inventoryRepo.setStock(product, 1)

        val cart = Cart(customer)
        cart.addItem(product, 2) // Attempting to buy 2

        // Act & Assert
        val exception = assertThrows(Exception::class.java) {
            orderProcessor.checkout(cart)
        }

        assertTrue(exception.message!!.contains("Not enough stock"))
    }

    @Test
    fun `should calculate total and apply taxes correctly`() {
        // Arrange
        val customer = Customer("C2", "Jane Smith", "jane@example.com")
        val product1 = Product("P1", "Laptop", 1000.0)
        val product2 = Product("P2", "Mouse", 50.0)

        inventoryRepo.setStock(product1, 10)
        inventoryRepo.setStock(product2, 10)

        val cart = Cart(customer)
        cart.addItem(product1, 1) // 1 * 1000.0 = 1000.0
        cart.addItem(product2, 2) // 2 * 50.0 = 100.0
        // Expected Subtotal: 1100.0
        // Expected Tax (10%): 110.0
        // Expected Total: 1210.0

        // Act
        val order = orderProcessor.checkout(cart)

        // Assert
        assertEquals(1100.0, order.subtotal, 0.0)
        assertEquals(110.0, order.tax, 0.0)
        assertEquals(1210.0, order.total, 0.0)
    }

    @Test
    fun `should register order in repository and reduce stock`() {
        // Arrange
        val customer = Customer("C3", "Alice", "alice@example.com")
        val product = Product("P3", "Keyboard", 100.0)
        inventoryRepo.setStock(product, 5)

        val cart = Cart(customer)
        cart.addItem(product, 2)

        // Act
        val order = orderProcessor.checkout(cart)

        // Assert
        // Verify the order was registered in the customer's history (OrderRepository)
        val savedOrders = orderRepo.getOrders()
        assertTrue("Repository should contain the created order", savedOrders.contains(order))
        assertEquals("The order customer should be Alice", "Alice", savedOrders.first().customer.name)

        // Verify the stock was accurately reduced (Started with 5, bought 2, should be 3)
        assertEquals(3, inventoryRepo.getStock(product))
    }

    // --- Helper Fake Classes for Testing ---

    // --- Helper Fake Classes for Testing ---

    class FakeInventoryRepository : InventoryRepository {
        private val stockMap = mutableMapOf<String, Int>()

        // This method acts as both our test helper and the interface's addStock (if it works the same way)
        override fun addStock(product: Product, quantity: Int) {
            val currentStock = getStock(product)
            stockMap[product.id] = currentStock + quantity
        }

        // Keep this helper for the test setup if you prefer, or just use addStock
        fun setStock(product: Product, quantity: Int) {
            stockMap[product.id] = quantity
        }

        override fun getStock(product: Product): Int {
            return stockMap[product.id] ?: 0
        }

        override fun reduceStock(product: Product, quantity: Int) {
            val currentStock = getStock(product)
            stockMap[product.id] = currentStock - quantity
        }
    }

    class FakeOrderRepository : OrderRepository {
        private val ordersList = mutableListOf<Order>()

        override fun saveOrder(order: Order) {
            ordersList.add(order)
        }

        override fun getCustomerHistory(customer: Customer): List<Order> {
            return ordersList.filter { it.customer.id == customer.id }
        }

        // Helper method to get all orders in the test
        fun getOrders(): List<Order> = ordersList
    }
}