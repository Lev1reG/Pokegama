package com.example.pokegama.ui.add

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.cloudinary.Cloudinary
import com.cloudinary.utils.ObjectUtils
import com.example.pokegama.BuildConfig
import com.example.pokegama.R
import com.example.pokegama.databinding.FragmentAddBinding
import com.example.pokegama.ui.adapter.DropdownAdapter
import com.example.pokegama.ui.dialogs.NoInternetDialogFragment
import com.example.pokegama.util.*
import dagger.hilt.android.AndroidEntryPoint
import com.github.dhaval2404.imagepicker.ImagePicker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class AddFragment : Fragment(R.layout.fragment_add) {

    private val binding by viewBinding(FragmentAddBinding::bind)
    private val viewModel: AddViewModel by viewModels()
    private lateinit var startForFacilityImageResult: ActivityResultLauncher<Intent>
    private var cloudinary = Cloudinary()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupDropdownAdapter()
        setupCloudinary()

        binding.addfacilityFacilityAutoComplete.doOnTextChanged{ text, _, _, _ ->
            viewModel.onTypeChange(text.toString())
        }

        binding.addfacilityNameTextView.doOnTextChanged{ text, _, _, _ ->
            viewModel.onNameChange(text.toString())
        }

        binding.addfacilityFacultyAutoComplete.doOnTextChanged{ text, _, _, _ ->
            viewModel.onFacultyChange(text.toString())
        }

        binding.addfacilityFacilityphotoButton.setOnClickListener {
            pickImageGallery()
        }

        registerImagePickerResult()

        binding.addfacilityDescriptionTextView.doOnTextChanged{ text, _, _, _ ->
            viewModel.onDescriptionChange(text.toString())
        }

        binding.addfacilitySubmit.setOnClickListener {
            onSubmit()
        }

        collectUiState()
        collectUiEvents()
    }

    private fun collectUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect {

                }
            }
        }
    }

    private fun collectUiEvents() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.events.collect { event ->
                    when (event) {
                        AddScreenEvents.ShowNoInternetDialog -> openNoInternetDialog()
                        is AddScreenEvents.ShowToast -> {
                            requireContext().showToast(event.message)
                        }
                    }
                }
            }
        }
    }

    private fun setupCloudinary(){
        val config = mapOf(
            "cloud_name" to BuildConfig.CLOUDINARY_NAME,
            "api_key" to BuildConfig.CLOUDINARY_API_KEY,
            "api_secret" to BuildConfig.CLOUDINARY_API_SECRET
        )
        cloudinary = Cloudinary(config)
    }

    private suspend fun uploadImageToCloudinary(fileUri: Uri): Boolean {
        val dateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
        val dateNow = dateFormat.format(Date())
        val publicId = "${viewModel.uiState.value.type}_${viewModel.uiState.value.name}_$dateNow"

        return withContext(Dispatchers.IO) {
            try {
                val inputStream = requireContext().contentResolver.openInputStream(fileUri)
                val uploadResult = cloudinary.uploader().upload(inputStream, ObjectUtils.asMap("public_id", publicId))
                val imageUrl = uploadResult["url"] as String
                viewModel.setFacilityImg(imageUrl)
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }


    private fun setupDropdownAdapter(){
        val facilityList = resources.getStringArray(R.array.facility_list)
        val facultyList = resources.getStringArray(R.array.faculty_list)
        DropdownAdapter.setupAdapter(requireContext(), binding.addfacilityFacilityAutoComplete, facilityList, R.layout.dropdown_item)
        DropdownAdapter.setupAdapter(requireContext(), binding.addfacilityFacultyAutoComplete, facultyList, R.layout.dropdown_item)
    }

    private fun pickImageGallery() {
        ImagePicker.with(this)
            .crop()
            .compress(1024)
            .maxResultSize(1080, 1080)
            .createIntent { intent ->
                startForFacilityImageResult.launch(intent)
            }
    }

    private fun registerImagePickerResult() {
        startForFacilityImageResult = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data

            if (resultCode == Activity.RESULT_OK) {
                val fileUri = data?.data
                if (fileUri != null) {
                    Uri.parse(fileUri.toString()).lastPathSegment?.let {
                        viewModel.setFacilityImgName(
                            it
                        )
                    }
                    binding.addfacilityFacilityphotoText.text = viewModel.uiState.value.facilityImgName
                    viewModel.setFileUri(fileUri)
                }
            } else if (resultCode == ImagePicker.RESULT_ERROR) {
                Toast.makeText(requireContext(), ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Task Cancelled", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun onSubmit(){
        lifecycleScope.launch {
            val fieldChecks = listOf(
                viewModel.uiState.value.type to "Masukkan jenis fasilitas",
                viewModel.uiState.value.name to "Masukkan nama tempat fasilitas",
                viewModel.uiState.value.faculty to "Masukkan fakultas tempat fasilitas",
                viewModel.uiState.value.facilityImgName to "Masukkan gambar fasilitas",
                viewModel.uiState.value.description to "Masukkan deskripsi fasilitas"
            )

            for ((value, message) in fieldChecks) {
                if (value.isEmpty()) {
                    viewModel.emitMessage(message)
                    return@launch
                }
            }

            val uploadSuccess = viewModel.uiState.value.fileUri?.let { fileUri ->
                uploadImageToCloudinary(fileUri)
            } ?: false

            if (!uploadSuccess) {
                viewModel.emitMessage("Gagal mengupload gambar")
                return@launch
            }

            viewModel.addFacility()
            Log.d("AddFragment", "Data : ${viewModel.uiState.value}")
            onSubmitSuccess()
        }
    }

    private fun onSubmitSuccess(){
        binding.addfacilityNameTextView.text.clear()
        binding.addfacilityFacilityAutoComplete.text.clear()
        binding.addfacilityFacultyAutoComplete.text.clear()
        binding.addfacilityFacilityphotoText.text = ""
        binding.addfacilityDescriptionTextView.text.clear()
    }

    private fun openNoInternetDialog() {
        NoInternetDialogFragment().show(parentFragmentManager, OPEN_NO_INTERNET_DIALOG)
    }
}