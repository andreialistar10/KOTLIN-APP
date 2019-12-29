package com.andrei.entities.components.data.remote

import com.andrei.entities.components.data.Product
import com.andrei.entities.core.Api
import retrofit2.http.*

//data class PagedProducts(val page: Int, val products: List<Product>, val more: Boolean) {}

object ProductApi {

    interface Service {

        @GET("academic-courses/entities")
        suspend fun findAll(): List<Product>

        @Headers("Content-Type: application/json")
        @GET("academic-courses/entities/{id}")
        suspend fun findOne(@Path("id") productId: Int): Product

        @Headers("Content-Type: application/json")
        @POST("academic-courses/entities")
        suspend fun add(@Body product: Product): Product

        @PUT("academic-courses/entities")
        suspend fun update(@Body product: Product): Unit
    }

    val service: Service = Api.retrofit.create(Service::class.java)
}