package com.example.messageapp.ui.addNewsScreen

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messageapp.core.ConstVariables
import com.example.messageapp.core.logD
import com.example.messageapp.domain.useCase.ApiServiceUseCase
import com.example.messageapp.domain.useCase.AppPreferencesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import javax.inject.Inject

@HiltViewModel
class AddNewsViewModel @Inject constructor(
    private val appPreference: AppPreferencesUseCase,
    private val apiServiceUseCase: ApiServiceUseCase,
) : ViewModel() {

    private var _userName: MutableStateFlow<String> = MutableStateFlow("")
    var userName: StateFlow<String> = _userName
    private var _image: MutableStateFlow<MultipartBody.Part?> = MutableStateFlow(null)
    var image: StateFlow<MultipartBody.Part?> = _image



    fun userNameAccount() {
        viewModelScope.launch(Dispatchers.IO) {
            _userName.value = appPreference.getString(ConstVariables.userName).toString()
        }
    }

    fun uploadNewsWithImage(part: MultipartBody.Part,
                            nameNews: RequestBody,
                            userName: RequestBody) {

        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (_image.value != null ) {
                    apiServiceUseCase.uploadNews(part, nameNews, userName)
//                    logD("Новость успешна отправлена")
                }else{
//                    logD("imagePart Не получен ")
                }
            } catch (e: Exception) {
                Log.e("ERROR", e.toString())
            }
        }
    }

    fun convertBitMapToPart(image: Bitmap) {
        try {
            val stream = ByteArrayOutputStream()
            image.compress(Bitmap.CompressFormat.JPEG, 80, stream)
            val byteArray = stream.toByteArray()
            val nameImage = image.config?.name

            val body = MultipartBody.Part.createFormData(
                "photo[content]", nameImage,
                byteArray.toRequestBody("image/*".toMediaTypeOrNull(), 0, byteArray.size)
            )
            _image.value = body
        } catch (e: Exception) {
            logD("convertBitMapToPart failed ")
        }
    }

    // todo доделать
    fun uploadNewsWithOutImage(){

    }

}