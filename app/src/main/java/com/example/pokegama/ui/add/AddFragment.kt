package com.example.pokegama.ui.add

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
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
import androidx.core.view.isVisible
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
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraBoundsOptions
import kotlinx.coroutines.launch
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.extension.style.style
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.gestures.addOnMapClickListener
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.mapbox.maps.CoordinateBounds

@AndroidEntryPoint
class AddFragment : Fragment(R.layout.fragment_add) {

    private lateinit var pointAnnotationManager: PointAnnotationManager

    private val binding by viewBinding(FragmentAddBinding::bind)
    private val viewModel: AddViewModel by viewModels()
    private lateinit var startForFacilityImageResult: ActivityResultLauncher<Intent>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupDropdownAdapter()

        binding.mapView.getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS) { style ->
            // Set a default camera position (zoom in on UGM)
            binding.mapView.getMapboxMap().setCamera(
                com.mapbox.maps.CameraOptions.Builder()
                    .center(Point.fromLngLat(110.37762, -7.77083))
                    .zoom(14.0)
                    .build()
            )

            val bitmap = BitmapFactory.decodeResource(resources, R.drawable.red_pin)
            style.addImage("red-pin-icon-id", bitmap)
            // Create PointAnnotationManager once the style is loaded
            pointAnnotationManager = binding.mapView.annotations.createPointAnnotationManager()


            // Add a click listener to get the latitude and longitude when the map is clicked
            binding.mapView.getMapboxMap().addOnMapClickListener { point ->
                val latitude = point.latitude()
                val longitude = point.longitude()

                // Clear existing markers if needed
                pointAnnotationManager.deleteAll()

                val pointAnnotationOptions = PointAnnotationOptions()
                    .withPoint(Point.fromLngLat(longitude, latitude))
                    .withIconImage("red-pin-icon-id")

                pointAnnotationManager.create(pointAnnotationOptions)

                viewModel.setLatitude(latitude)
                viewModel.setLongitude(longitude)

                viewModel.emitMessage("Lat: ${viewModel.uiState.value.latitude}, Lng: ${viewModel.uiState.value.longitude}")

                true
            }
        }

        binding.addfacilityFacilityAutoComplete.doOnTextChanged { text, _, _, _ ->
            viewModel.onTypeChange(text.toString())
        }

        binding.addfacilityNameTextView.doOnTextChanged { text, _, _, _ ->
            viewModel.onNameChange(text.toString())
        }

        binding.addfacilityFacultyAutoComplete.doOnTextChanged { text, _, _, _ ->
            viewModel.onFacultyChange(text.toString())
        }

        binding.addfacilityFacilityphotoButton.setOnClickListener {
            pickImageGallery()
        }

        registerImagePickerResult()

        binding.addfacilityDescriptionTextView.doOnTextChanged { text, _, _, _ ->
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
                    binding.loadingLayout.loadingLayout.isVisible = it.isLoading
                    binding.addfacilitySubmit.isEnabled = !it.isLoading
                    binding.addfacilityFacilityphotoButton.isEnabled = !it.isLoading
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

    private fun setupDropdownAdapter() {
        val facilityList = resources.getStringArray(R.array.facility_list)
        val facultyList = resources.getStringArray(R.array.faculty_list)
        DropdownAdapter.setupAdapter(
            requireContext(),
            binding.addfacilityFacilityAutoComplete,
            facilityList,
            R.layout.dropdown_item
        )
        DropdownAdapter.setupAdapter(
            requireContext(),
            binding.addfacilityFacultyAutoComplete,
            facultyList,
            R.layout.dropdown_item
        )
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
                    binding.addfacilityFacilityphotoText.text =
                        viewModel.uiState.value.facilityImgName
                    viewModel.setFileUri(fileUri)
                }
            } else if (resultCode == ImagePicker.RESULT_ERROR) {
                Toast.makeText(requireContext(), ImagePicker.getError(data), Toast.LENGTH_SHORT)
                    .show()
            } else {
                Toast.makeText(requireContext(), "Task Cancelled", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun onSubmit() {
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
                viewModel.uploadImageToCloudinary(requireContext().contentResolver, fileUri)
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

    private fun onSubmitSuccess() {
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