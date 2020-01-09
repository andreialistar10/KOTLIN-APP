package com.andrei.entities.auth.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "TOKEN")
data class TokenHolder(

    @PrimaryKey @ColumnInfo(name = "jwt") val jwt: String,
    @ColumnInfo(name = "role") val role: String
)