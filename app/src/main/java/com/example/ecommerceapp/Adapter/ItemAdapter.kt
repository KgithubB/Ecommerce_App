package com.example.ecommerceapp.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ecommerceapp.Activity.ProductDetails

import com.example.ecommerceapp.Model.Item.Data
import com.example.ecommerceapp.R


class ItemAdapter(var con:Context, var list:List<Data>):RecyclerView.Adapter<ItemAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txt = itemView.findViewById<TextView>(R.id.text1)
        val image = itemView.findViewById<ImageView>(R.id.imageView)
    }


    fun setFilteredList(list: List<Data>){
        this.list = list
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view = LayoutInflater.from(con).inflate(R.layout.item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.count()
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val album = list[position]
        holder.txt.text = album.category_name
        val BASE_URL = "http://magento2.mydevfactory.com:8007"
        Glide.with(con).load(BASE_URL + list[position].image).into(holder.image)

        holder.itemView.setOnClickListener{
            val intent = Intent(con, ProductDetails::class.java)
            intent.putExtra("item_id",album.id)
            con.startActivity(intent)
        }
    }
}

