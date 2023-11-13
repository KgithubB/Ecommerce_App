package com.example.ecommerceapp.Activity

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ecommerceapp.AllInterface.SignUpInterface
import com.example.ecommerceapp.Model.SignIn.signin
import com.example.ecommerceapp.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SignInActivity : AppCompatActivity() {

    private lateinit var signUpInterface: SignUpInterface
    private lateinit var username: EditText
    private lateinit var password: EditText
    private lateinit var btn: Button
    private lateinit var sharedPreferences: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        username = findViewById(R.id.editEmailSignIN)
        password = findViewById(R.id.editPassSignIn)
        btn = findViewById(R.id.signInBtn)


        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE)

        btn.setOnClickListener {
            val retrofit = Retrofit.Builder()
                .baseUrl("http://magento2.mydevfactory.com:8007")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            signUpInterface = retrofit.create(SignUpInterface::class.java)
            createPost()

        }


        val signUpText: TextView =findViewById(R.id.signUpText)
        signUpText.setOnClickListener{
            val intent = Intent(this,SignUpActivity::class.java)
            startActivity(intent)
        }

    }


    private fun createPost() {
        val call: Call<signin> = signUpInterface.createPost(username.text.toString(), password.text.toString())

        call.enqueue(object : Callback<signin> {
            override fun onResponse(call: Call<signin>, response: Response<signin>) {
                if (response.isSuccessful) {
                    val jwtToken = response.body()?.access

                    // Save the JWT token in SharedPreferences
                    saveJwtToken(jwtToken)

                    Toast.makeText(this@SignInActivity, "Sign In Successful", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@SignInActivity, "Sign In Failed", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<signin>, t: Throwable) {
                Toast.makeText(this@SignInActivity, "Sign In Failed: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun saveJwtToken(token: String?) {
        val editor = sharedPreferences.edit()
        editor.putString("jwt_token", token)
        editor.commit()
        val intent = Intent(this@SignInActivity,ItemListActivity::class.java)
        startActivity(intent)
    }


    private fun getJwtToken(): String? {
        return sharedPreferences.getString("jwt_token", null)
    }




}
