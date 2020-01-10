package com.andrei.entities.core

import com.andrei.entities.auth.data.TokenHolder
import okhttp3.Interceptor
import okhttp3.Response

class TokenInterceptor constructor() : Interceptor{

    var tokenHolder: TokenHolder? = null

    override fun intercept(chain: Interceptor.Chain): Response {

        val original = chain.request()
        val originalUrl = original.url
        if (tokenHolder == null)
            return chain.proceed(original)
        val token = tokenHolder
        val requestBuilder = original.newBuilder()
            .addHeader("Authorization","Bearer ${token?.jwt}")
            .url(originalUrl)
        val request = requestBuilder.build()
        return chain.proceed(request)
    }

}