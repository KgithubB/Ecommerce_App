package com.example.ecommerceapp.Activity

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ecommerceapp.Adapter.ItemAdapter
import com.example.ecommerceapp.AllInterface.SignUpInterface
import com.example.ecommerceapp.Model.Item.Data
import com.example.ecommerceapp.Model.Item.item
import com.example.ecommerceapp.R
import com.google.android.material.navigation.NavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Locale

class ItemListActivity : AppCompatActivity() {
    private val BASE_URL = "http://magento2.mydevfactory.com:8007/"
    private lateinit var recyclerView: RecyclerView
    private lateinit var itemAdapter: ItemAdapter
    private lateinit var searchView: SearchView
    private var dataList = ArrayList<Data>()

    // for sidenavigation
    lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_list)



        //for sidenavigation
        val drawerLayout :DrawerLayout = findViewById(R.id.drawerLayout)
        val navView :NavigationView = findViewById(R.id.nav_view)
        toggle = ActionBarDrawerToggle(this,drawerLayout,R.string.open,R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        navView.setNavigationItemSelectedListener {

            when(it.itemId){
                R.id.profile -> Toast.makeText(applicationContext,"Clicked Profile",Toast.LENGTH_SHORT).show()
                R.id.categories -> Toast.makeText(applicationContext,"Clicked Category",Toast.LENGTH_SHORT).show()
                R.id.cart -> Toast.makeText(applicationContext,"Clicked cart",Toast.LENGTH_SHORT).show()
                R.id.wishlist -> Toast.makeText(applicationContext,"Clicked Wishlist",Toast.LENGTH_SHORT).show()
                R.id.oreder -> Toast.makeText(applicationContext,"Clicked Order",Toast.LENGTH_SHORT).show()
                R.id.setting-> Toast.makeText(applicationContext,"Clicked Setting",Toast.LENGTH_SHORT).show()
                R.id.feedback -> Toast.makeText(applicationContext,"Clicked Feddback",Toast.LENGTH_SHORT).show()
                R.id.share -> Toast.makeText(applicationContext,"Clicked Share",Toast.LENGTH_SHORT).show()
                R.id.rate-> Toast.makeText(applicationContext,"Clicked Rate",Toast.LENGTH_SHORT).show()
                R.id.logout -> Toast.makeText(applicationContext,"Clicked Logout",Toast.LENGTH_SHORT).show()

            }
            true
        }



        recyclerView = findViewById(R.id.recyclerView)
        searchView = findViewById(R.id.search)
        recyclerView.layoutManager = LinearLayoutManager(this,RecyclerView.VERTICAL,false)
        itemAdapter = ItemAdapter(this, dataList)
        recyclerView.adapter = itemAdapter


// searchview function
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterList(newText)
                return true
            }
        })

        getAllData()
    }

//for sidenavigation
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
    if(toggle.onOptionsItemSelected(item)){
        return true
    }
        return super.onOptionsItemSelected(item)
    }



    // fetch the data from api

    private fun getAllData() {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val signUpInterface = retrofit.create(SignUpInterface::class.java)

        signUpInterface.getData().enqueue(object : Callback<item> {
            override fun onResponse(call: Call<item>, response: Response<item>) {
                if (response.isSuccessful) {
                    val data = response.body()
                    data?.let {
                        dataList.addAll(it.data)
                        itemAdapter.notifyDataSetChanged()
                    }
                } else {
                    Log.e("ItemListActivity", "Response unsuccessful")
                }
            }

            override fun onFailure(call: Call<item>, t: Throwable) {
                Log.e("ItemListActivity", "Request failed", t)
                Toast.makeText(this@ItemListActivity, "Failed to fetch data", Toast.LENGTH_SHORT).show()
            }
        })
    }


    // for filter the search result
    private fun filterList(query: String?) {
        if (query != null) {
            val filteredList = ArrayList<Data>()
            val lowerCaseQuery = query.lowercase(Locale.ROOT)
            for (dataItem in dataList) {
                val lowerCaseCategoryName = dataItem.category_name.lowercase(Locale.ROOT)
                if (lowerCaseCategoryName.contains(lowerCaseQuery)) {
                    filteredList.add(dataItem)
                }
            }

            if (filteredList.isEmpty()) {
                Toast.makeText(this, "No Data found", Toast.LENGTH_SHORT).show()
            } else {
                itemAdapter.setFilteredList(filteredList)
            }
            itemAdapter.setFilteredList(filteredList)
        }
    }
}

