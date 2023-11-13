package com.example.ecommerceapp.Activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.ecommerceapp.AllInterface.SignUpInterface
import com.example.ecommerceapp.Model.SignUp.signup
import com.example.ecommerceapp.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SignUpActivity : AppCompatActivity() {

    private lateinit var signUpInterface: SignUpInterface
    lateinit var username: EditText
    lateinit var emailid: EditText
    lateinit var password: EditText
    lateinit var btn:Button
    private lateinit var sharedPreferences: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)


        username = findViewById(R.id.editNameSignUp)
        emailid = findViewById(R.id.editEmailSignUp)
        password = findViewById(R.id.editPassSignUp)
        btn = findViewById(R.id.signUpBtn)

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE)

        btn.setOnClickListener{
            val retrofit = Retrofit.Builder()
                .baseUrl("http://magento2.mydevfactory.com:8007/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            signUpInterface = retrofit.create(SignUpInterface::class.java)
            createPost()

        }


        val signInText: TextView =findViewById(R.id.signInText)
        signInText.setOnClickListener{
            val intent = Intent(this,SignInActivity::class.java)
            startActivity(intent)
        }

    }


    private fun createPost(){
        val call: Call<signup> = signUpInterface.signUp(username.text.toString(),emailid.text.toString(),password.text.toString())
        call.enqueue(object : Callback<signup> {
            override fun onResponse(call: Call<signup>, response: Response<signup>) {

                if (response.isSuccessful) {
                    val jwtToken = response.body()?.message

                    // Save the JWT token in SharedPreferences
                    saveJwtToken(jwtToken)

                    Toast.makeText(this@SignUpActivity, "Sign up Successful", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@SignUpActivity, "Sign up Failed", Toast.LENGTH_SHORT).show()
                }

            }

            override fun onFailure(call: Call<signup>, t: Throwable) {
                Toast.makeText(this@SignUpActivity,t.message, Toast.LENGTH_SHORT).show()

            }
        })
    }

    private fun saveJwtToken(token: String?) {
        val editor = sharedPreferences.edit()
        editor.putString("jwt_token", token)
        editor.commit()
        val intent = Intent(this@SignUpActivity,SignInActivity::class.java)
        startActivity(intent)
    }


    private fun getJwtToken(): String? {
        return sharedPreferences.getString("jwt_token", null)
    }


}
