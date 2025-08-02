package com.example.messageapp.ui.addNewsScreen

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.messageapp.R
import com.example.messageapp.core.logD
import com.example.messageapp.data.network.model.NewsRequest
import com.example.messageapp.databinding.FragmentAddNewsBinding
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.RequestBody
import kotlin.random.Random
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File


@AndroidEntryPoint
class AddNewsFragment : Fragment() {

    lateinit var binding: FragmentAddNewsBinding
    private val viewModel: AddNewsViewModel by viewModels()

    // todo переделать
    private lateinit var image: Bitmap
    private lateinit var imagePickerLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imagePickerLauncher =
            registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
                uri?.let { context?.contentResolver?.openInputStream(it) }.use { input ->
                    val bitMap = BitmapFactory.decodeStream(input)
                    image = bitMap

                    viewModel.convertBitMapToPart(image)
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddNewsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        viewModel.getUserName()

        binding.button4.setOnClickListener {

            imagePickerLauncher.launch("image/*")

        }

        binding.imageView9.setOnClickListener {
            if (viewModel.imagePart.value !=  null) {
//                val newsRequest = NewsRequest(
//                    id = Random.nextInt(),
//                    userName = viewModel.userName.value.toString(),
//                    image = viewModel.imagePart.value!!,
//                    text = binding.editTextText3.text.toString()
//                )
//                logD(newsRequest.toString())

                val nameNews = binding.editTextText3.text.toString().toRequestBody()
                val userName = viewModel.userName.value?.toString()?.toRequestBody()

                if (userName != null) {
                    viewModel.sendImage(viewModel.imagePart.value!!, nameNews, userName)
                }
                findNavController().navigate(R.id.action_addNewsFragment_to_newsListFragment)
            }else{

            }
        }
    }



}