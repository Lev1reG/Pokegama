package com.example.pokegama.ui.add

import android.app.Activity
import android.content.Intent
import androidx.fragment.app.viewModels
import android.os.Bundle
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
import com.example.pokegama.R
import com.example.pokegama.databinding.FragmentAddBinding
import com.example.pokegama.ui.adapter.DropdownAdapter
import com.example.pokegama.ui.dialogs.NoInternetDialogFragment
import com.example.pokegama.util.*
import dagger.hilt.android.AndroidEntryPoint
import com.github.dhaval2404.imagepicker.ImagePicker
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddFragment : Fragment(R.layout.fragment_add) {

    private val binding by viewBinding(FragmentAddBinding::bind)
    private val viewModel: AddViewModel by viewModels()
    private lateinit var startForFacilityImageResult: ActivityResultLauncher<Intent>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupDropdownAdapter()

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
        binding.addfacilityFacilityphotoText.doOnTextChanged{ text, _, _, _ ->
            viewModel.onFacilityImgChange(text.toString())
        }

        binding.addfacilityDescriptionTextView.doOnTextChanged{ text, _, _, _ ->
            viewModel.onDescriptionChange(text.toString())
        }

        binding.addfacilitySubmit.setOnClickListener {
            lifecycleScope.launch {
                val isSuccess = viewModel.onSubmitButtonClicked()
                if (isSuccess) onSubmitSuccess()
            }
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
                    binding.addfacilityFacilityphotoText.text = fileUri.toString()
                }
            } else if (resultCode == ImagePicker.RESULT_ERROR) {
                Toast.makeText(requireContext(), ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Task Cancelled", Toast.LENGTH_SHORT).show()
            }
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