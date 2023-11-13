package com.example.ecommerceapp.Adapter


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ecommerceapp.Model.Products.bag
import com.example.ecommerceapp.R


class CartAdapter(var con:Context, var list:List<bag>, private val quantityChangeListener: (bag, Int) -> Unit):RecyclerView.Adapter<CartAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val image = itemView.findViewById<ImageView>(R.id.image)
        val text = itemView.findViewById<TextView>(R.id.text)
        val quantitySpinner = itemView.findViewById<Spinner>(R.id.count)

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(con).inflate(R.layout.cartlayout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.count()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = list[position]
        val BASE_URL = "http://magento2.mydevfactory.com:8007"
        Glide.with(con).load(BASE_URL + list[position].thumbnail).into(holder.image)
        holder.text.text = item.title

        // Set up the Spinner
        val quantityOptions = arrayOf(1, 2, 3, 4, 5) // You can customize the options
        val adapter = ArrayAdapter(con, android.R.layout.simple_spinner_item, quantityOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        holder.quantitySpinner.adapter = adapter

        // Set an item selected listener for the Spinner
        holder.quantitySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                val newQuantity = quantityOptions[pos]
                // Call the quantity change callback
                quantityChangeListener(item, newQuantity)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }

        }


        // Set the initial Spinner selection based on the item's quantity
        val initialQuantityPosition = quantityOptions.indexOf(item.quantity)
        holder.quantitySpinner.setSelection(initialQuantityPosition)


    }
}
