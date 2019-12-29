package com.andrei.entities.components.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class Product(

    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "price") var price: Int
){
    @PrimaryKey @ColumnInfo(name = "id") var id = 0
    constructor(id: Int, name: String, price: Int) : this(name,price){
        this.id = id
    }

    override fun toString(): String = name
}