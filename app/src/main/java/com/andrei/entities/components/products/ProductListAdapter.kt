package com.andrei.entities.components.products

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.andrei.entities.R
import com.andrei.entities.components.data.Product
import com.andrei.entities.components.product.ProductEditFragment
import kotlinx.android.synthetic.main.product_view.view.*

class ProductListAdapter(private val fragment: Fragment) :
    RecyclerView.Adapter<ProductListAdapter.ViewHolder>() {

    var products = emptyList<Product>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    private var onProductClick: View.OnClickListener

    init {
        onProductClick = View.OnClickListener { view ->

            val product = view.tag as Product
            fragment.findNavController().navigate(R.id.product_edit_fragment, Bundle().apply {
                putInt(ProductEditFragment.PRODUCT_ID, product.id)
                putInt(ProductEditFragment.PRODUCT_PRICE, product.price)
            })
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.product_view, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val product = products[position]
        holder.textView.text = product.name
        holder.itemView.tag = product
        holder.itemView.setOnClickListener(onProductClick)
    }

    override fun getItemCount() = products.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.text
    }
}