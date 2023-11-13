
package com.example.ecommerceapp.Activity

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ecommerceapp.Adapter.CartAdapter
import com.example.ecommerceapp.AllInterface.SignUpInterface
import com.example.ecommerceapp.Model.Products.bag
import com.example.ecommerceapp.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class CartActivity : AppCompatActivity() {
    private val BASE_URL = "http://magento2.mydevfactory.com:8007"
    private lateinit var recyclerView: RecyclerView
    private lateinit var cartAdapter: CartAdapter
    private val cartItems = mutableListOf<bag>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Retrieve added item IDs from local storage
        val sharedPreferences = getSharedPreferences("CartItems", Context.MODE_PRIVATE)
        val cartItemIds = sharedPreferences.getStringSet("items", mutableSetOf()) ?: mutableSetOf()


        // Fetch item details for each item ID and display them in the RecyclerView
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SignUpInterface::class.java)



        for (itemIdStr in cartItemIds) {
            val itemId = itemIdStr.toInt()
            val retroData = retrofit.getProductDetails(itemId)
            retroData.enqueue(object : Callback<bag> {
                override fun onResponse(call: Call<bag>, response: Response<bag>) {
                    if (response.isSuccessful) {
                        val data = response.body()
                        data?.let {
                            // Update the quantity from SharedPreferences
                            val quantity = sharedPreferences.getInt("quantity_$itemId", 1)
                            it.quantity = 1
                            cartItems.add(it)
                            if (cartItems.size == cartItemIds.size) {
                                // Initialize the CartAdapter with cart items
                                cartAdapter = CartAdapter(this@CartActivity, cartItems){ item, newQuantity ->
                                    // Handle quantity changes here
                                    item.quantity = newQuantity
                                    // Update your local storage or perform any other necessary actions
                                    sharedPreferences.edit().putInt("quantity_$itemId", newQuantity).apply()
                                }
                                recyclerView.adapter = cartAdapter
                            }
                        }
                    } else {
                        // Handle unsuccessful response
                    }
                }

                override fun onFailure(call: Call<bag>, t: Throwable) {
                    // Handle network request failure
                }
            })
        }


    }
}
