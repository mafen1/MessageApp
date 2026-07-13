package com.example.messageapp.data.security

import android.util.Base64
import com.example.messageapp.domain.security.Base64Codec
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AndroidBase64Codec @Inject constructor() : Base64Codec {

    override fun encode(bytes: ByteArray): String = Base64.encodeToString(bytes, Base64.DEFAULT)

    override fun decode(encoded: String): ByteArray = Base64.decode(encoded, Base64.DEFAULT)
}
