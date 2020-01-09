package com.andrei.entities.components.products

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.andrei.entities.R
import com.andrei.entities.auth.data.AuthRepository
import com.andrei.entities.components.data.Product
import com.andrei.entities.core.TAG
import com.andrei.entities.core.synchronizing.MessageWorker
import com.andrei.entities.core.synchronizing.WebSocketApi
import com.andrei.entities.core.synchronizing.notification.MyNotification
import com.andrei.entities.core.synchronizing.notification.Type
import kotlinx.android.synthetic.main.product_list_fragment.*

class ProductListFragment : Fragment() {

    private lateinit var productListAdapter: ProductListAdapter
    private lateinit var productsViewModel: ProductsViewModel
    private val myMessageWorker: MyMessageWorker = MyMessageWorker()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        Log.v(TAG, "onCreateView")
        WebSocketApi.connectToWebSocket(myMessageWorker)
        return inflater.inflate(R.layout.product_list_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {

        super.onActivityCreated(savedInstanceState)
        Log.v(TAG, "onActivityCreated")
        if (!AuthRepository.isLoggedIn){
            findNavController().navigate(R.id.login_fragment)
            return
        }
        setupProductList()
        fab.setOnClickListener {

            Log.v(TAG, "add new product")
            findNavController().navigate(R.id.product_edit_fragment)
        }
    }

    override fun onDestroyView() {

        Log.v(TAG,"onDestroyView")
        WebSocketApi.disconnect()
        super.onDestroyView()
    }

    private fun setupProductList() {

        productListAdapter = ProductListAdapter(this)
        productsViewModel = ViewModelProviders.of(this).get(ProductsViewModel::class.java)
        productsList.adapter = productListAdapter

        productsViewModel.products.observe(this, Observer { value ->
            Log.v(TAG, "update products")
            productListAdapter.products = value
        })

        productsViewModel.loading.observe(this, Observer { loading ->
            Log.v(TAG, "update loading")
            progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        })

        productsViewModel.loadingError.observe(this, Observer { exception ->
            if (exception != null) {
                Log.v(TAG, "update loading error")
                val message = "Loading exception ${exception.message}"
                val parentActivity = activity?.parent
                if (parentActivity != null)
                    Toast.makeText(activity?.parent, message, Toast.LENGTH_SHORT).show()
            }
        })
        productsViewModel.refresh()
    }

    private inner class MyMessageWorker:MessageWorker{

        override fun onMessageArrived(notification: MyNotification) {

            notification.entity.let {
                val product = Product(it.id,it.name,it.price)
                when(notification.type){
                    Type.ADD -> notifyAddProduct(product)
                    Type.UPDATE -> notifyUpdateProduct(product)
                }
            }
        }

        private fun notifyAddProduct(product: Product){

            Log.v(TAG,"Add notification arrived...")
            productsViewModel.addProduct(product)
        }

        private fun notifyUpdateProduct(product: Product){

            Log.v(TAG,"Update notification arrived...")
            productsViewModel.updateProduct(product)
        }
    }
}