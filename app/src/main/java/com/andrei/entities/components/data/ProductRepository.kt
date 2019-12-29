package com.andrei.entities.components.data

import androidx.lifecycle.LiveData
import com.andrei.entities.components.data.local.ProductDao
import com.andrei.entities.core.Result
import com.andrei.entities.components.data.remote.ProductApi

class ProductRepository(private val productDao: ProductDao) {

    val products = productDao.getAll()

    suspend fun refresh(): Result<Boolean>{
        try{
            val products = ProductApi.service.findAll()
            for (product in products){
                productDao.insert(product)
            }
            return Result.Success(true)
        } catch (e: java.lang.Exception){
            return Result.Error(e)
        }
    }

//    suspend fun loadAll(): Result<List<Product>> {
//
//        if (cachedProducts != null) {
//            return Result.Success(cachedProducts as List<Product>)
//        }
//        return try {
//            val products = ProductApi.service.findAll()
//            cachedProducts = mutableListOf()
//            cachedProducts?.addAll(products)
//            Result.Success(cachedProducts as List<Product>) //this will be returned
//        } catch (e: Exception) {
//            Result.Error(e) //this will be returned
//        }
//    }

    fun getById(productId: Int): LiveData<Product>{
        return productDao.getById(productId)
    }

//    suspend fun loadOne(productId: Int): Result<Product> {
//
//        val product = cachedProducts?.find { it.id == productId }
//        if (product != null) {
//            return Result.Success(product)
//        }
//        return try {
//            Result.Success(ProductApi.service.findOne(productId))
//        } catch (e: Exception) {
//            Result.Error(e)
//        }
//    }

    suspend fun save(product: Product): Result<Product> {

        return try {
            val savedProduct = ProductApi.service.add(product)
            productDao.insert(savedProduct)
            Result.Success(savedProduct)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun update(product: Product): Result<Product> {

        return try {
            //val index = cachedProducts?.indexOfFirst { it.id == product.id }
            ProductApi.service.update(product)
//            if (index != null)
//                cachedProducts?.set(index, product)
            productDao.update(product)
            return Result.Success(product)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}