package com.andrei.entities.components.data

import androidx.lifecycle.LiveData
import com.andrei.entities.auth.data.TokenHolder
import com.andrei.entities.auth.data.local.AuthDao
import com.andrei.entities.components.data.local.ProductDao
import com.andrei.entities.components.data.local.ProductIndex
import com.andrei.entities.components.data.remote.ProductApi
import com.andrei.entities.core.Result

class ProductRepository(private val productDao: ProductDao, private val authDao: AuthDao) {

    suspend fun getAll(): Result<List<Product>> {

        return try {
            val offlineProducts = productDao.getAll()
            Result.Success(offlineProducts)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun refresh(): Result<List<Product>> {

        return try {
            sendUnsavedProductsToBackendServer()
            val products = ProductApi.service.findAll()
            refreshDatabase(products)
            Result.Success(products)
        } catch (e: java.lang.Exception) {
            Result.Error(e)
        }
    }

    fun getById(productId: Int): LiveData<Product> {
        return productDao.getById(productId)
    }

    suspend fun save(product: Product): Result<Product> {

        return try {
            val savedProduct = ProductApi.service.add(product)
            productDao.insertSavedProduct(savedProduct)
            Result.Success(savedProduct)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun saveLocalStorage(product: Product): Result<Product> {

        return try {
            if (product.id != 0) {
                productDao.insertSavedProduct(product)
            } else {
                productDao.insertUnsavedProduct(product)
            }
            Result.Success(product)
        } catch (e: java.lang.Exception) {
            Result.Error(e)
        }
    }

    suspend fun update(product: Product): Result<Product> {

        return try {
            ProductApi.service.update(product)
            productDao.update(product)
            return Result.Success(product)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun updateLocalStorage(product: Product): Result<Product> {

        return try {
            productDao.update(product)
            return Result.Success(product)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun addToken(tokenHolder: TokenHolder): Result<TokenHolder> {

        return try {
            authDao.saveToken(tokenHolder)
            return Result.Success(tokenHolder)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    fun getTokenHolder(): Result<TokenHolder> {

        return try {
            val liveDataToken = authDao.getTokenHolder()
            Result.Success(liveDataToken)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun clearDatabase(){

        deleteAll()
        deleteToken()
        deleteCurrentIndex()
    }

    private suspend fun refreshDatabase(products: List<Product>) {

        productDao.initTables(products)
    }

    private suspend fun sendUnsavedProductsToBackendServer() {

        val unsavedProducts = productDao.getAllUnsavedProducts()
        unsavedProducts.forEach { product ->
            product.id = 0
            ProductApi.service.add(product)
        }
    }

    private suspend fun deleteToken(): Result<Nothing?> {

        return try {
            authDao.deleteToken()
            Result.Success(null)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    private suspend fun deleteAll(): Result<Nothing?> {

        return try {
            productDao.deleteAll()
            Result.Success(null)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    private suspend fun deleteCurrentIndex(): Result<Nothing?>{

        return try {
            productDao.deleteCurrentIndex()
            Result.Success(null)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}