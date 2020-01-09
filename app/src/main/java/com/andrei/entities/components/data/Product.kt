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
    @ColumnInfo(name = "saved") var saved = true
    constructor(id: Int, name: String, price: Int) : this(name,price){
        this.id = id
    }

    constructor(id: Int, name: String, price: Int, saved:Boolean) : this(id,name,price){
        this.saved = saved
    }

    override fun toString(): String = name

    override fun equals(other: Any?): Boolean {

        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as Product

        return other.id == this.id
    }

    override fun hashCode(): Int {

        var result = name.hashCode()
        result = 31 * result + price
        result = 31 * result + id
        return result
    }
}