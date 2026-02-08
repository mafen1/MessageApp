package com.example.messageapp.ui.addNewsScreen

import android.R.attr.description
import android.R.attr.text
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.messageapp.R
import com.example.messageapp.core.logD
import com.example.messageapp.core.snackBar
import com.example.messageapp.data.network.model.NewsRequest
import com.example.messageapp.databinding.FragmentAddNewsBinding
import com.example.messageapp.ui.BaseFragment
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.Calendar


@AndroidEntryPoint
class AddNewsFragment : BaseFragment<FragmentAddNewsBinding>(FragmentAddNewsBinding::inflate) {

    private val viewModel: AddNewsViewModel by viewModels()

    private var image: Bitmap? = null
//    private lateinit var imagePickerLauncher: ActivityResultLauncher<String>
    private var imagePickerLauncher: ActivityResultLauncher<String>? = null
    // todo private
    var imageUri : Uri? = null

    private val navFragmentArgs: AddNewsFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        imagePickerLauncher =
            registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
                imageUri = uri
                uri?.let { context?.contentResolver?.openInputStream(it) }.use { input ->
                    val bitMap = BitmapFactory.decodeStream(input)
                    image = bitMap

                    if (image != null){
                        viewModel.convertBitMapToPart(image!!)
                    }

                }
            }
    }

    override fun initView() {
        viewModel.userNameAndNameAccount()

        binding.toolbar.setOnMenuItemClickListener{ menuItem ->
            when(menuItem.itemId){
                R.id.menuAddImage -> {
                    try {
                        imagePickerLauncher?.launch("image/*")
                    }catch (e: Exception){
                        logD("Не создан экземпляр imagePickerLauncher ${e.toString()}")
                    }

                    true
                }

                else -> false
            }
        }
        binding.toolbar.setNavigationOnClickListener {
           navigateToNewsList()
        }

        binding.addImageButton.setOnClickListener {
            uploadNews()
        }

    }

    private fun uploadNews() {
        val nameNews = binding.postText.text.toString()
        val userName = viewModel.userName.value



        logD("$nameNews addNewsFragment")
        logD("$userName addNewsFragment")

        if (viewModel.image.value != null) {

            val newsRequest = NewsRequest(
                userNameAuthor = userName ?: "",
                nameAuthor = viewModel.nameUser.value ?: "",
                date = Calendar.getInstance().time.toString(),
                countLike = 0,
                countComment = 0,
                avatarAuthor = "",
                description = binding.postText.text.toString(),
                comment = listOf()
            )
            logD("ADD news $newsRequest")
            logD("ADD news1 ${viewModel.image.value!!}")

            val gsonNews = Gson().toJson(newsRequest).toRequestBody()


            viewModel.uploadNewsWithImage(
                viewModel.image.value!!,
                gsonNews
            )

            navigateToNewsList()

        } else {
            try {
                val newsRequest = NewsRequest(
                    userNameAuthor = userName ?: "",
                    nameAuthor = viewModel.nameUser.value ?: "",
                    date = Calendar.getInstance().time.toString(),
                    countLike = 0,
                    countComment = 0,
                    avatarAuthor = "",
                    description = binding.postText.text.toString(),
                    comment = listOf()
                )
                viewModel.uploadNewsWithOutImage(
                    userName = userName!!,
                    description = nameNews
                )
                navigateToNewsList()
            } catch (e: Exception) {
                logD(e.toString())
            }
        }
    }

    private fun navigateToNewsList() {
        try {
            val action = AddNewsFragmentDirections.actionAddNewsFragmentToNewsListFragment(navFragmentArgs.Userr)
            findNavController().navigate(action)
        } catch (_: Exception) {
            try {
                findNavController().navigate(R.id.newsListFragment)
            } catch (_: Exception) {
                findNavController().popBackStack()
            }
        }
    }
}

