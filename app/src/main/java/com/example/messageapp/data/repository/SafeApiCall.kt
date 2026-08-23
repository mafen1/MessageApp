package com.example.messageapp.data.repository

import android.util.Log
import android.util.MalformedJsonException
import com.example.messageapp.BuildConfig
import com.google.gson.Gson
import com.google.gson.JsonParseException
import kotlinx.coroutines.CancellationException
import retrofit2.HttpException

/**
 * Единая обёртка сетевых вызовов: транслирует ошибки транспорта/сериализации
 * в Result с человекочитаемым сообщением сервера, не глотает отмену корутины.
 */
internal suspend fun <T> safeApiCall(apiCall: suspend () -> T): Result<T> {
    return try {
        val result = apiCall()
        if (BuildConfig.DEBUG) {
            Log.d("API_SUCCESS", "Response: $result")
        }
        Result.success(result)
    } catch (e: HttpException) {
        val errorBody = try { e.response()?.errorBody()?.string() } catch (_: Exception) { null }
        val serverMessage = try {
            if (!errorBody.isNullOrBlank()) {
                val json = Gson().fromJson(errorBody, com.google.gson.JsonObject::class.java)
                when {
                    json.has("message") -> json.get("message").asString
                    json.has("error") -> json.get("error").asString
                    else -> errorBody
                }
            } else null
        } catch (_: Exception) { errorBody }
        val message = serverMessage?.takeIf { it.isNotBlank() } ?: e.message ?: "HTTP ${e.code()}"
        Log.e("API_ERROR", "API call failed ${e.code()}: $message body=$errorBody", e)
        Result.failure(Exception(message, e))
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        if (e is JsonParseException || e is MalformedJsonException) {
            Log.e("JSON_ERROR", "Malformed JSON received. Check network logs for raw response", e)
        }
        Log.e("API_ERROR", "API call failed: ${e.message}", e)
        Result.failure(e)
    }
}
