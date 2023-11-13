package com.example.ecommerceapp.Model.Products

data class bag(
    val category_name: String,
    val desc: String,
    val id: Int,
    val images: ArrayList<Image>,
    val in_stock: Boolean,
    val price: String,
    val rating: Double,
    val reviews:ArrayList<Review>,
    val shipping_charge: String,
    val suggested_products:ArrayList<String>,
    val thumbnail: String,
    val title: String,
    var quantity: Int

)

