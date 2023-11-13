package com.example.ecommerceapp.AllInterface

import com.example.ecommerceapp.Model.Cart.cart
import com.example.ecommerceapp.Model.Item.item
import com.example.ecommerceapp.Model.Products.bag
import com.example.ecommerceapp.Model.SignIn.signin
import com.example.ecommerceapp.Model.SignUp.signup
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path


interface SignUpInterface {
    @FormUrlEncoded
    @POST("signup/")
    fun signUp(
        @Field("username") username: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<signup>

    @FormUrlEncoded
    @POST("/auth/jwt/create")
    fun createPost(
        @Field("username") username: String,
        @Field("password") password: String
    ): Call<signin>

    @GET("category/")
    fun getData(): Call<item>

    @GET("product_detail/{id}/")
    fun getProductDetails(@Path("id") productId:Int):Call<bag>

    @FormUrlEncoded
    @POST("add_to_cart/")
    fun addCopyCart(
        @Field("product_id") product_id:Int,
        @Field("quantity") quantity: Int
    ): Call<cart>

}
