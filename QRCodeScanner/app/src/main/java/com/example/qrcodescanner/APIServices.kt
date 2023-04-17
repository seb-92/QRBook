package com.example.qrcodescanner

import retrofit2.Call
import retrofit2.http.*

interface APIServices {
    @POST("system/user/register")
    fun newUser(@Body newUser: Client) : Call<ResponseObject>

    @GET("system/user/{login}/{password}")
    fun login(@Path(value = "login", encoded = true) login: String, @Path(value = "password", encoded = true) password: String) : Call<Client>

    @GET("/system/book/id/{bookId}")
    fun bookInfo(@Path(value = "bookId", encoded = true) bookId: Long) : Call<Book>

    @GET("/system/userHistory/{id}/RETURN")
    fun bookHistory(@Path(value = "id", encoded = true) id: Long) : Call<List<HistoryBook>>

    @GET("/system/book/current/{userId}")
    fun userInfo(@Path(value = "userId", encoded = true) userId: Long) : Call<List<Book>>

    @POST("/system/book/order")
    fun orderBook(@Body orderBook: OrderBook) : Call<ResponseObject>

    @POST("/system/book/return")
    fun returnBook(@Body orderBook: OrderBook) : Call<ResponseObject>

    @GET("/system/book/author/{author}")
    fun searchAuthor(@Path(value = "author", encoded = true) author: String) : Call<List<Book>>

    @GET("/system/book/title/{title}")
    fun searchTitle(@Path(value = "title", encoded = true) title: String) : Call<List<Book>>

    @GET("/system/book/yearOfPublish/{yearOfPublish}")
    fun searchYear(@Path(value = "yearOfPublish", encoded = true) yearOfPublish: String) : Call<List<Book>>

}

data class DeleteResponse(var response: String)