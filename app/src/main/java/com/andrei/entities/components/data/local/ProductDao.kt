package com.andrei.entities.components.data.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.andrei.entities.auth.data.TokenHolder
import com.andrei.entities.components.data.Product

@Dao
interface ProductDao {

    @Query("SELECT * FROM products ORDER BY NAME ASC")
    suspend fun getAll(): List<Product>

    @Query("SELECT * FROM products WHERE id=:id")
    fun getById(id: Int): LiveData<Product>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(product: Product)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(product: Product)

    @Query("DELETE FROM products")
    suspend fun deleteAll()

    @Query(value = "SELECT * FROM products WHERE saved = 0 ORDER BY id")
    suspend fun getAllUnsavedProducts(): List<Product>

    @Query(value = "SELECT * FROM productIndex LIMIT 1")
    suspend fun getCurrentIndex(): ProductIndex

    @Query("UPDATE productIndex SET current_index = :index")
    suspend fun updateProductIndex(index: Int)

    @Query("DELETE FROM productIndex")
    suspend fun deleteCurrentIndex()

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addCurrentIndex(productIndex: ProductIndex)

    @Transaction
    suspend fun insertUnsavedProduct (product:Product){

        val productIndex = getCurrentIndex()
        product.id = productIndex.index
        product.saved = false
        insert(product)
        productIndex.index++
        updateProductIndex(productIndex.index)
    }

    @Transaction
    suspend fun insertSavedProduct (product: Product){

        product.saved = true
        insert(product)
        updateProductIndex(product.id + 1)
    }

    @Query("SELECT * FROM TOKEN LIMIT 1")
    fun getTokenHolder(): TokenHolder

    @Query("DELETE FROM TOKEN")
    suspend fun deleteToken()

    @Query("UPDATE TOKEN SET jwt = :jwt")
    suspend fun updateToken(jwt: String)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun saveToken(tokenHolder: TokenHolder)
}