package com.andrei.entities.components.data.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.andrei.entities.components.data.Product

@Dao
interface ProductDao {

    @Query("SELECT * FROM products ORDER BY NAME ASC")
    fun getAll(): LiveData<List<Product>>

    @Query("SELECT * FROM products WHERE id=:id")
    fun getById(id: Int): LiveData<Product>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(product: Product)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(product: Product)

    @Query("DELETE FROM products")
    suspend fun deleteAll()
}