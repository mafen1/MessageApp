package com.example.messageapp.ui.addNewsScreen

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.messageapp.R
import com.example.messageapp.core.snackBar
import com.example.messageapp.databinding.FragmentAddNewsBinding
import com.example.messageapp.ui.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.RequestBody.Companion.toRequestBody


@AndroidEntryPoint
class AddNewsFragment : BaseFragment<FragmentAddNewsBinding>(FragmentAddNewsBinding::inflate) {

    private val viewModel: AddNewsViewModel by viewModels()

    private lateinit var image: Bitmap
    private lateinit var imagePickerLauncher: ActivityResultLauncher<String>

    private val navFragmentArgs: AddNewsFragmentArgs by navArgs()

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

    override fun initView() {
        viewModel.userNameAccount()

        binding.toolbar.setOnMenuItemClickListener{ menuItem ->
            when(menuItem.itemId){
                R.id.menuAddImage -> {
                    imagePickerLauncher.launch("image/*")
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
        if (viewModel.image.value != null) {

            val nameNews = binding.textInputLayout.editText?.text.toString().toRequestBody()
            val userName = viewModel.userName.value.toRequestBody()

            viewModel.uploadNewsWithImage(viewModel.image.value!!, nameNews, userName)
            navigateToNewsList()
        } else {
            snackBar(binding.root, "Загрузите картинку")
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

