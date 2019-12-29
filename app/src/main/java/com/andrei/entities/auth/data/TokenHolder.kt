package com.andrei.entities.auth.data

data class TokenHolder(
    val jwt: String,
    val role: String
)