package com.example.secondchance

data class Seller(
    val sellerId: String,
    val name: String,
    val phone: String,
    val address: String,
    val products: List<Product>
)
