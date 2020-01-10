package com.andrei.entities.auth.data

import com.andrei.entities.auth.data.remote.RemoteAuthDataSource
import com.andrei.entities.core.Api
import com.andrei.entities.core.Result

object AuthRepository {

    var user: User? = null
        private set

    val isLoggedIn: Boolean
        get() = Api.tokenInterceptor.tokenHolder != null

    init {
        user = null
    }

    fun logout() {
        Api.tokenInterceptor.tokenHolder = null
    }

    suspend fun login(username: String, password: String): Result<TokenHolder> {
        val user = User(username, password)
        val result = RemoteAuthDataSource.login(user)
        if (result is Result.Success<TokenHolder>) {
            setLoggedInUser(user, result.data)
        }
        return result
    }

    private fun setLoggedInUser(user: User, tokenHolder: TokenHolder) {

        this.user = user
        Api.tokenInterceptor.tokenHolder = tokenHolder
    }
}