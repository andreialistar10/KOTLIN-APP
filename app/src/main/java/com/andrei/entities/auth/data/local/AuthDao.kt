package com.andrei.entities.auth.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.andrei.entities.auth.data.TokenHolder

@Dao
interface AuthDao {

    @Query("SELECT * FROM TOKEN LIMIT 1")
    fun getTokenHolder(): TokenHolder

    @Query("DELETE FROM TOKEN")
    suspend fun deleteToken()

    @Query("UPDATE TOKEN SET jwt = :jwt")
    suspend fun updateToken(jwt: String)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun saveToken(tokenHolder: TokenHolder)
}