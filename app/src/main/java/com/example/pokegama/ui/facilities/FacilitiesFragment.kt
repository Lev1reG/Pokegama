package com.example.pokegama.ui.facilities

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import androidx.fragment.app.Fragment
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.*
import com.example.pokegama.R
import com.example.pokegama.data.model.local.Facility
import com.example.pokegama.databinding.FragmentFacilitiesBinding
import com.example.pokegama.ui.adapter.FacilitiesAdapter
import com.example.pokegama.ui.dialogs.NoInternetDialogFragment
import com.example.pokegama.util.*
import com.google.gson.JsonObject
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraBoundsOptions
import com.mapbox.maps.CoordinateBounds
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.annotation.OnAnnotationClickListener
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.CircleAnnotation
import com.mapbox.maps.plugin.annotation.generated.CircleAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.CircleAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.OnCircleAnnotationClickListener
import com.mapbox.maps.plugin.annotation.generated.createCircleAnnotationManager
import com.mapbox.maps.plugin.gestures.addOnMapClickListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FacilitiesFragment : Fragment(R.layout.fragment_facilities) {
    private var facilityType: String? = null
    private val binding by viewBinding(FragmentFacilitiesBinding::bind)
    private val viewModel by viewModels<FacilitiesViewModel>()
    private val facilityAdapter = FacilitiesAdapter()
    private val internetChecker: InternetChecker by lazy { InternetChecker(requireContext()) }

    private lateinit var circleAnnotationManager: CircleAnnotationManager

    @SuppressLint("ClickableViewAccessibility")
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

        binding.mapView.setOnTouchListener { mapView, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                    binding.facilitiesScrollView.requestDisallowInterceptTouchEvent(true)
                }
                MotionEvent.ACTION_UP -> {
                    binding.facilitiesScrollView.requestDisallowInterceptTouchEvent(false)
                    mapView.performClick()
                }
                MotionEvent.ACTION_CANCEL -> {
                    binding.facilitiesScrollView.requestDisallowInterceptTouchEvent(false)
                }
            }
            false
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

    private fun createAllAnnotation(facilityItems: List<Facility>){
        circleAnnotationManager.deleteAll()

        facilityItems.forEach { facility ->
            val circleAnnotationOptions = CircleAnnotationOptions()
                .withPoint(Point.fromLngLat(facility.longitude, facility.latitude))
                .withCircleRadius(6.0)
                .withCircleColor(ContextCompat.getColor(requireContext(), R.color.purple_400))
                .withCircleStrokeWidth(3.0)
                .withCircleStrokeColor(ContextCompat.getColor(requireContext(), R.color.white))
                .withData(JsonObject().apply {
                    addProperty("name", facility.name)
                    addProperty("type", facility.type)
                })
            circleAnnotationManager.create(circleAnnotationOptions)
        }

        var currentDisplayedAnnotation: CircleAnnotation? = null

        circleAnnotationManager.addClickListener(OnCircleAnnotationClickListener { annotation ->
            val data = annotation.getData()
            val facilityName = data?.asJsonObject?.get("name")?.asString
            val facilityType = data?.asJsonObject?.get("type")?.asString
            val text = "$facilityType $facilityName"

            if (currentDisplayedAnnotation != null) {
                binding.popupLayout.popupLayout.isVisible = false
                currentDisplayedAnnotation = null
            }

            binding.popupLayout.popupLayout.isVisible = true
            binding.popupLayout.locationName.text = text
            val screenLocation = binding.mapView.mapboxMap
                .pixelForCoordinate(annotation.point)
            binding.popupLayout.popupLayout.x = screenLocation.x.toFloat() - binding.popupLayout.popupLayout.width / 2
            binding.popupLayout.popupLayout.y = screenLocation.y.toFloat() - binding.popupLayout.popupLayout.height

            currentDisplayedAnnotation = annotation

            Log.d("FacilitiesFragment","$facilityName")
            true
        })

        binding.mapView.mapboxMap.subscribeCameraChanged {
            binding.popupLayout.popupLayout.isVisible = false
            currentDisplayedAnnotation = null
        }

        binding.mapView.mapboxMap.addOnMapClickListener {
            binding.popupLayout.popupLayout.isVisible = false
            currentDisplayedAnnotation = null
            true
        }
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
                    createAllAnnotation(it.facilityItems)
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
        circleAnnotationManager = binding.mapView.annotations.createCircleAnnotationManager()
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