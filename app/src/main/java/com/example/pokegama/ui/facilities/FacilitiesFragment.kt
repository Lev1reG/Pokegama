package com.example.pokegama.ui.facilities

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.*
import com.example.pokegama.R
import com.example.pokegama.databinding.FragmentFacilitiesBinding
import com.example.pokegama.ui.adapter.FacilitiesAdapter
import com.example.pokegama.ui.dialogs.NoInternetDialogFragment
import com.example.pokegama.util.*
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraBoundsOptions
import com.mapbox.maps.CoordinateBounds
import com.mapbox.maps.Style
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FacilitiesFragment : Fragment(R.layout.fragment_facilities) {
    private var facilityType: String? = null
    private val binding by viewBinding(FragmentFacilitiesBinding::bind)
    private val viewModel by viewModels<FacilitiesViewModel>()
    private val facilityAdapter = FacilitiesAdapter()
    private val internetChecker: InternetChecker by lazy { InternetChecker(requireContext()) }

    private fun handleMapView() {
        val ugmBounds = CoordinateBounds(
            Point.fromLngLat(110.36288772392231, -7.785549599115427), // Southwest corner
            Point.fromLngLat(110.39603271480053, -7.758165106236248)  // Northeast corner
        )

        val isOnline = internetChecker.hasInternetConnection()

        if (isOnline) {
            binding.mapView.mapboxMap.loadStyle(Style.MAPBOX_STREETS) { style ->
                val bitmap = BitmapFactory.decodeResource(resources, R.drawable.red_pin)
                style.addImage("red-pin-icon-id", bitmap)
            }
        } else {
            binding.mapView.mapboxMap.loadStyle(Style.LIGHT) { style ->
                val bitmap = BitmapFactory.decodeResource(resources, R.drawable.red_pin)
                style.addImage("red-pin-icon-id", bitmap)
            }
        }

        binding.mapView.mapboxMap.setBounds(
            CameraBoundsOptions.Builder()
                .bounds(ugmBounds)
                .minZoom(14.0)
                .build()
        )

        binding.mapView.mapboxMap.setCamera(
            com.mapbox.maps.CameraOptions.Builder()
                .center(Point.fromLngLat(110.37762, -7.77083))
                .zoom(14.0)
                .build()
        )
    }

    private fun collectFacilityType() {
        arguments?.let {
            facilityType = it.getString("facility_type")
            viewModel.setFacilityTypeAndLoad(facilityType ?: "")
            Log.d("FacilitiesFragment", "Facility Type: $facilityType")
            binding.titleFacilities.text = facilityType
        }
    }

    private fun collectUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect {
                    binding.loadingLayout.loadingLayout.isVisible = it.isLoading
                    facilityAdapter.submitList(it.facilityItems)
                }
            }
        }
    }

    private fun collectUiEvents() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.events.collect { event ->
                    when (event) {
                        FacilitiesScreenEvents.ShowNoInternetDialog -> openNoInternetDialog()
                        is FacilitiesScreenEvents.ShowToast -> {
                            requireContext().showToast(event.message)
                        }
                    }
                }
            }
        }
    }

    private fun initRecyclerView() {
        binding.facilityRv.apply {
            adapter = facilityAdapter
            setHasFixedSize(false)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handleMapView()
        collectFacilityType()
        initRecyclerView()
        collectUiState()
        collectUiEvents()
    }

    private fun openNoInternetDialog() {
        NoInternetDialogFragment().show(parentFragmentManager, OPEN_NO_INTERNET_DIALOG)
    }
}