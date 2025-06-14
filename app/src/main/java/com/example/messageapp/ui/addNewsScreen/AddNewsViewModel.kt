package com.example.messageapp.ui.addNewsScreen

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messageapp.core.ConstVariables
import com.example.messageapp.core.logD
import com.example.messageapp.data.network.model.NewsRequest
import com.example.messageapp.domain.useCase.ApiServiceUseCase
import com.example.messageapp.domain.useCase.AppPreferencesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class AddNewsViewModel @Inject constructor(
    private val appPreference: AppPreferencesUseCase,
    private val apiServiceUseCase: ApiServiceUseCase,
) : ViewModel() {

    private var _userName: MutableLiveData<String> = MutableLiveData()
    var userName: MutableLiveData<String> = _userName

    private var _imagePart: MutableLiveData<MultipartBody.Part> = MutableLiveData()
    var imagePart: LiveData<MultipartBody.Part> = _imagePart


    fun addNews(newsRequest: NewsRequest) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                apiServiceUseCase.addNews(newsRequest)
            } catch (e: Exception) {
                logD(e.toString())
            }
        }
    }

    fun getUserName() {
        viewModelScope.launch(Dispatchers.IO) {
            _userName.postValue(appPreference.getValueString(ConstVariables.userName))
        }
    }

    fun sendImage(newsRequest: NewsRequest) {

        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (_imagePart.value != null ) {
                    apiServiceUseCase.sendImage(_imagePart.value!!, newsRequest)
                    logD("Файл сохранен")
                }else{
                    logD("imagePart Не получен ")
                }
            } catch (e: Exception) {
                Log.e("ERROR", e.toString())
            }
        }
    }

    fun convertBitMapToPart(image: Bitmap) {
        val stream = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.JPEG, 80, stream)
        val byteArray = stream.toByteArray()
        val body = MultipartBody.Part.createFormData(
            "photo[content]", Random.nextInt().toString(),
            byteArray.toRequestBody("image/*".toMediaTypeOrNull(), 0, byteArray.size)
        )
        _imagePart.value = body
    }

}