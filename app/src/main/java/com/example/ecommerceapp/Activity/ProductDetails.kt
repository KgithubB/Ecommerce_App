package com.example.ecommerceapp.Activity


import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ecommerceapp.Adapter.ProductAdapter
import com.example.ecommerceapp.AllInterface.SignUpInterface
import com.example.ecommerceapp.Model.Cart.cart
import com.example.ecommerceapp.Model.Products.bag
import com.example.ecommerceapp.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ProductDetails : AppCompatActivity() {

    // intialize all variables

    private val BASE_URL = "http://magento2.mydevfactory.com:8007"
    private lateinit var recyclerView: RecyclerView
    private lateinit var productAdapter: ProductAdapter
    lateinit var textViewName : TextView
    lateinit var textViewName1 : TextView
    lateinit var textViewName2 : TextView
    lateinit var textViewName3 : TextView
    lateinit var textViewName4 : TextView
    lateinit var imageViewThumb : ImageView
    lateinit var textViewName6 : TextView
    private var cartValue = 0
    private lateinit var signUpInterface: SignUpInterface
    private lateinit var sharedPreferences: SharedPreferences
    private var isItemInCart = false // Flag to track item in cart state
    private lateinit var addToCartButton :Button

    // declare the itemId
    private var itemId = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_details)

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("CartPreferences", Context.MODE_PRIVATE)


        // initialize the layout names

       textViewName = findViewById(R.id.name)
        textViewName1 = findViewById(R.id.Description)
        textViewName2 = findViewById(R.id.Amount)
        textViewName3 = findViewById(R.id.Ratings)
       textViewName4 = findViewById(R.id.shipping)
        imageViewThumb = findViewById(R.id.Thumb)
       textViewName6 = findViewById(R.id.headline)

        val cartValueTextView = findViewById<TextView>(R.id.cartValue)
         addToCartButton = findViewById(R.id.button)


        // access the item id here
        itemId = intent.getIntExtra("item_id",0)

        // Retrieve cart value from SharedPreferences
        cartValue = sharedPreferences.getInt("cartValue", 0)
        cartValueTextView.text = cartValue.toString()

        // navigate to cartactivity

        val cartIcon:ImageView =findViewById(R.id.cartIcon)
      cartIcon.setOnClickListener{
            val intent = Intent(this,CartActivity::class.java)
            startActivity(intent)
        }

        // Check if the item is already in the cart (You need to implement this function)
        isItemInCart = checkIfItemIsInCart(itemId) // Implement this function

        // Set the initial text of the cartButton based on item in cart state
        updateCartButtonText()


        addToCartButton.setOnClickListener {

            if (isItemInCart) {
                // if the item is in the cart , navigate to the cart activity
                val intent = Intent(this, CartActivity::class.java)
                startActivity(intent)
            } else {
                // if the item is not in the cart add it to the cart
                cartValue++
                cartValueTextView.text = cartValue.toString()
                addToCart(itemId)


                // Store the added item's details locally for the cart
                val sharedPreferences = getSharedPreferences("CartItems", Context.MODE_PRIVATE)
                val cartItems =
                    sharedPreferences.getStringSet("items", mutableSetOf()) ?: mutableSetOf()
                cartItems.add("$itemId")

                // update the sharedpreference
                sharedPreferences.edit().putStringSet("items", cartItems).apply()

                // update the button text after adding to the cart
                isItemInCart = true
                updateCartButtonText()

            }
        }


        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this,RecyclerView.HORIZONTAL,false)


//        Log.e("itemId" , itemId.toString())


        // Fetch and display product data
        getAllData(itemId)
    }



    // update the cartButton text based on item in cart state
    private fun updateCartButtonText(){
        if (isItemInCart) {
            addToCartButton.text = "Go To Cart"
        } else {
            addToCartButton.text = "Add To Cart"
        }
    }


    // Check if the item is in the cart
    private fun checkIfItemIsInCart(itemId: Int): Boolean {
        val sharedPreferences = getSharedPreferences("CartItems", Context.MODE_PRIVATE)
        val cartItems = sharedPreferences.getStringSet("items", mutableSetOf()) ?: mutableSetOf()
        return cartItems.contains("$itemId")
    }



    private fun getAllData(itemId: Int) {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SignUpInterface::class.java)

        val retroData = retrofit.getProductDetails(itemId)

        retroData.enqueue(object : Callback<bag>{
            override fun onResponse(call: Call<bag>, response: Response<bag>) {
                if (response.isSuccessful) {
                    val data = response.body()

                    Log.e("imgSize" , response.body()?.images?.size.toString())

                    if (data != null) {
                        // Initialize the ProductAdapter with the product data
                        productAdapter = ProductAdapter(this@ProductDetails, data.images)
                        recyclerView.adapter = productAdapter

                        textViewName.text = data.category_name
                        textViewName1.text = data.desc
                        textViewName2.text = data.price
                        textViewName3.text = data.rating.toString()
                        textViewName4.text = data.shipping_charge
                        Glide.with(this@ProductDetails).load(BASE_URL+data.thumbnail).into(imageViewThumb)
                        textViewName6.text = data.title

                    } else {
                        // Handle the case where data is null
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




    private fun addToCart(itemId: Int) {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        signUpInterface = retrofit.create(SignUpInterface::class.java)

        val call: Call<cart> = signUpInterface.addCopyCart(itemId, 1) // Assuming 1 item to be added
        call.enqueue(object : Callback<cart> {
            override fun onResponse(call: Call<cart>, response: Response<cart>) {
                if (response.isSuccessful) {
                    // Handle successful response here
                    Toast.makeText(
                        this@ProductDetails, "Product added to cart successfully", Toast.LENGTH_SHORT).show()
                    // Update cart value in SharedPreferences
                    val editor = sharedPreferences.edit()
                    editor.putInt("cartValue", cartValue)
                    editor.apply()


                } else {
                    // Handle error response here
                    Toast.makeText(this@ProductDetails, "Failed to add product to cart", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<cart>, t: Throwable) {
                // Handle network request failure
                Toast.makeText(this@ProductDetails, "Failed to add product to cart", Toast.LENGTH_SHORT).show()
            }
        })
    }

}




