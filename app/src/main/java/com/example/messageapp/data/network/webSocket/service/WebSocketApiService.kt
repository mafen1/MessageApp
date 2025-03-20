package com.example.messageapp.data.network.webSocket.service

import android.database.Observable
import okhttp3.ResponseBody
import retrofit2.http.GET

interface WebSocketApiService {
    fun connect()
    fun disconnect()
    fun send(message: String): String
}