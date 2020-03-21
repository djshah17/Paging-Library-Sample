package com.example.paginglibrarysample.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class User(

    @SerializedName("id")
    val id: Int,

    @SerializedName("email")
    val email: String,

    @SerializedName("first_name")
    val firstName: String,

    @SerializedName("last_name")
    val lastName: String

) : Serializable