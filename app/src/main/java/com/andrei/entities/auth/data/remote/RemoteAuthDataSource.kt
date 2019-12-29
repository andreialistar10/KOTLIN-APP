package com.andrei.entities.auth.data.remote

import android.util.Log
import com.andrei.entities.auth.data.TokenHolder
import com.andrei.entities.auth.data.User
import com.andrei.entities.core.Api
import java.lang.Exception
import com.andrei.entities.core.Result
import com.andrei.entities.core.TAG
import retrofit2.http.*

object RemoteAuthDataSource{

    interface AuthService{
        @FormUrlEncoded
        @POST("/academic-courses/login")
        suspend fun login(
            @Field("username") username: String,
            @Field("password") password: String
        ): TokenHolder
    }

    private val authService: AuthService = Api.retrofit.create(AuthService::class.java)

    suspend fun login(user: User): Result<TokenHolder>{

        try{
            Log.v(TAG, user.username + " " + user.password)
            return Result.Success(authService.login(user.username,user.password))
        } catch (e:Exception){
            Log.w("Error", e.message);
            return Result.Error(e)
        }
    }
}