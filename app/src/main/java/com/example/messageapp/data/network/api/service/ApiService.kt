package com.example.messageapp.data.network.api.service

import com.example.messageapp.data.model.User
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @POST("/newUser")
    fun addUser(
    user: String
    )
}