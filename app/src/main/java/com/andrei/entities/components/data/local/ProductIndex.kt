package com.andrei.entities.components.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "productIndex")
data class ProductIndex(
    @PrimaryKey @ColumnInfo(name = "current_index") var index:Int
)