package com.example.paginglibrarysample.api

import com.example.paginglibrarysample.models.User
import com.example.paginglibrarysample.models.UserResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("users")
    fun getUsers(@Query ("page") page:Int): Call<UserResponse>

}