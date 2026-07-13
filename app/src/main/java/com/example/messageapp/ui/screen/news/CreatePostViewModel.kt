package com.example.messageapp.ui.screen.news

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messageapp.core.ConstVariables
import com.example.messageapp.core.logD
import com.example.messageapp.domain.model.NewsPost
import com.example.messageapp.domain.usecase.AppPreferencesUseCase
import com.example.messageapp.domain.usecase.CreatePostUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import javax.inject.Inject

@HiltViewModel
class CreatePostViewModel @Inject constructor(
    private val appPreference: AppPreferencesUseCase,
    private val createPostUseCase: CreatePostUseCase
) : ViewModel() {

    private var _userName: MutableStateFlow<String?> = MutableStateFlow(null)
    var userName: StateFlow<String?> = _userName

    private var _nameUser: MutableStateFlow<String?> = MutableStateFlow(null)
    var nameUser: StateFlow<String?> = _nameUser

    private var _image: MutableStateFlow<ByteArray?> = MutableStateFlow(null)
    var image: StateFlow<ByteArray?> = _image

    fun userNameAndNameAccount() {
        viewModelScope.launch(Dispatchers.IO) {
            _userName.value = appPreference.getString(ConstVariables.userName).first()
            _nameUser.value = appPreference.getString(ConstVariables.nameUser).first()
        }
    }

    fun uploadNews(post: NewsPost, imageBytes: ByteArray?) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = createPostUseCase(post, imageBytes)
                if (result.isSuccess) {
                    logD("Новость успешна отправлена")
                } else {
                    logD("Ошибка отправки новости: ${result.exceptionOrNull()}")
                }
            } catch (e: Exception) {
                Log.e("ERROR", e.toString())
            }
        }
    }

    fun convertBitmapToBytes(bitmap: Bitmap) {
        try {
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream)
            _image.value = stream.toByteArray()
        } catch (e: Exception) {
            logD("convertBitmapToBytes failed ")
        }
    }
}
