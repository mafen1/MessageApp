package com.example.messageapp.store

import android.content.Context
import android.content.SharedPreferences
import android.util.Log

class SharedPreference(context: Context) {

    private val PREFS_NAME = "DATA"
    private val sharedPreference: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun save(keyName: String, value: String) {
        val editor = sharedPreference.edit()
        editor.putString(keyName, value)
        editor.apply()

        Log.d("TAG", sharedPreference.getString("tokenJwt", "")!!)
    }

    fun getValueString(keyName: String): String? {
        return sharedPreference.getString(keyName, "")
    }


}