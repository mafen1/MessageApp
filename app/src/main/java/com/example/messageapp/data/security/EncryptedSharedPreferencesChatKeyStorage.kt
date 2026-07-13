package com.example.messageapp.data.security

import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.example.messageapp.domain.security.ChatKeyStorage
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EncryptedSharedPreferencesChatKeyStorage @Inject constructor(
    @ApplicationContext private val context: Context
) : ChatKeyStorage {

    private val prefs: SharedPreferences by lazy {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        EncryptedSharedPreferences.create(
            context,
            PREFS_FILE,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    override fun getChatKey(chatId: String): ByteArray? {
        val encoded = prefs.getString(chatId, null) ?: return null
        return Base64.decode(encoded, Base64.DEFAULT)
    }

    override fun saveChatKey(chatId: String, key: ByteArray) {
        prefs.edit()
            .putString(chatId, Base64.encodeToString(key, Base64.DEFAULT))
            .apply()
    }

    private companion object {
        private const val PREFS_FILE = "chat_keys"
    }
}
