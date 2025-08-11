package com.example.messageapp.store

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.edit
import com.example.messageapp.domain.repo.preferences.AppPreference
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject


class SharedPreference @Inject constructor(@ApplicationContext context: Context) : AppPreference {

    private val PREFS_NAME = "DATA"
    private val sharedPreference: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    override fun save(keyName: String, value: String) {
        sharedPreference.edit {
            putString(keyName, value)
        }

        Log.d("TAG", sharedPreference.getString("tokenJwt", "")!!)
    }

    override fun getValueString(keyName: String): String? {
        return sharedPreference.getString(keyName, "")
    }


}