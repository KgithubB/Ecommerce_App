package com.example.ecommerceapp.Model.Products

data class Review(
    val date: String,
    val id: Int,
    val product: Int,
    val rating: String,
    val review_text: String,
    val user: String
)
