package com.example.messageapp.ui.addNewsScreen

import android.content.Intent
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
import com.example.messageapp.data.network.model.NewsRequest
import com.example.messageapp.databinding.FragmentAddNewsBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlin.random.Random


@AndroidEntryPoint
class AddNewsFragment : Fragment() {

    lateinit var binding: FragmentAddNewsBinding
    private val viewModel: AddNewsViewModel by viewModels()

    // todo переделать
    private lateinit var bitMap1: Bitmap
    private lateinit var imagePickerLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imagePickerLauncher =
            registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
                val file = createTempFile()
                uri?.let { context?.contentResolver?.openInputStream(it) }.use { input ->

                    requireContext().contentResolver
                    val bitMap = BitmapFactory.decodeStream(input)
                    bitMap1 = bitMap
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

            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "image/*"
            }

            imagePickerLauncher.launch("image/*")
        }

        binding.imageView9.setOnClickListener {

            val newsRequest = NewsRequest(
                id = Random.nextInt(),
                userName = viewModel.userName.value.toString(),
                text = binding.editTextText3.text.toString()
            )

            viewModel.sendImage(
                bitMap1,
                newsRequest
            )
        }
    }

}