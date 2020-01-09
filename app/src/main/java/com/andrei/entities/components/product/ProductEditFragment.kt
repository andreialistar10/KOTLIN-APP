package com.andrei.entities.components.product

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
import com.andrei.entities.components.data.Product
import com.andrei.entities.core.TAG
import kotlinx.android.synthetic.main.product_edit_fragment.*

class ProductEditFragment : Fragment() {

    companion object {
        const val PRODUCT_ID = "PRODUCT_ID"
        const val PRODUCT_PRICE = "PRODUCT_PRICE"
    }

    private lateinit var productEditViewModel: ProductEditViewModel
    private var productId: Int? = null
    //    private var productPrice: Int? = null
    private var product: Product? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        Log.v(TAG, "onCreate")
        super.onCreate(savedInstanceState)
        arguments?.let {
            if (it.containsKey(PRODUCT_ID)) {
                productId = it.getInt(PRODUCT_ID)
            }
//            if (it.containsKey(PRODUCT_PRICE)) {
//                productPrice = it.getInt(PRODUCT_PRICE)
//            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        Log.v(TAG, "onCreateView")
        return inflater.inflate(R.layout.product_edit_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        Log.v(TAG, "onViewCreated")
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {

        super.onActivityCreated(savedInstanceState)
        Log.v(TAG, "onActivityCreated")
        setupProductEditViewModel()
        fab.setOnClickListener {
            Log.v(TAG, "save product")
            val prod = product
            if (prod != null) {
                prod.name = product_name.text.toString()
                val price = product_price.text.toString().toIntOrNull() ?: return@setOnClickListener
                prod.price = price
                productEditViewModel.saveOrUpdateProduct(prod)
            }
        }
    }

    private fun setupProductEditViewModel() {

        productEditViewModel = ViewModelProviders.of(this).get(ProductEditViewModel::class.java)
        productEditViewModel.fetching.observe(this, Observer { fetching ->
            Log.v(TAG, "update fetching")
            progressBar.visibility = if (fetching) View.VISIBLE else View.GONE
        })
        productEditViewModel.exception.observe(this, Observer { exception ->
            if (exception != null) {
                Log.v(TAG, "update error")
                val message = "exception ${exception.message}"
                val parent = activity?.parent
                if (parent != null) {
                    Toast.makeText(parent, message, Toast.LENGTH_SHORT).show()
                }
            }
        })
        productEditViewModel.completed.observe(this, Observer { completed ->
            if (completed) {
                Log.v(TAG, "completed, navigate back")
                findNavController().popBackStack()
            }
        })
        val id = productId
        if (id == null)
            product = Product(0, "", 0)
        else {
            productEditViewModel.getProductById(id).observe(this, Observer {
                Log.v(TAG, "update products")
                if (it != null) {
                    product = it
                    product_name.setText(it.name)
                    product_price.setText(it.price.toString())
                }
            })
        }
    }
}