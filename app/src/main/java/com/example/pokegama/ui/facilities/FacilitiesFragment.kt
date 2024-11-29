package com.example.pokegama.ui.facilities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.MotionEvent
import androidx.fragment.app.Fragment
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
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
import com.mapbox.maps.ImageHolder
import com.mapbox.maps.MapboxMap
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.LocationPuck2D
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.CircleAnnotation
import com.mapbox.maps.plugin.annotation.generated.CircleAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.CircleAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.OnCircleAnnotationClickListener
import com.mapbox.maps.plugin.annotation.generated.createCircleAnnotationManager
import com.mapbox.maps.plugin.gestures.addOnMapClickListener
import com.mapbox.maps.plugin.locationcomponent.LocationComponentPlugin
import com.mapbox.maps.plugin.locationcomponent.location
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import com.mapbox.geojson.LineString
import com.mapbox.maps.extension.style.layers.generated.lineLayer
import com.mapbox.maps.extension.style.sources.generated.geoJsonSource
import com.mapbox.api.directions.v5.models.DirectionsResponse
import com.mapbox.api.directions.v5.DirectionsCriteria
import com.mapbox.api.directions.v5.MapboxDirections
import com.mapbox.api.directions.v5.models.RouteOptions
import com.mapbox.maps.extension.style.layers.addLayer
import com.mapbox.maps.extension.style.sources.addSource
import com.mapbox.maps.plugin.PuckBearing
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response



@AndroidEntryPoint
class FacilitiesFragment : Fragment(R.layout.fragment_facilities) {
    private var facilityType: String? = null
    private val binding by viewBinding(FragmentFacilitiesBinding::bind)
    private val viewModel by viewModels<FacilitiesViewModel>()
    private val facilityAdapter = FacilitiesAdapter()

    private val routeSourceId = "route-source"
    private val routeLayerId = "route-layer"
    private val internetChecker: InternetChecker by lazy { InternetChecker(requireContext()) }

    private lateinit var handler: Handler
    private lateinit var mapboxMap: MapboxMap
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
                mapboxMap = binding.mapView.mapboxMap
                enableLocationComponent()
            }
        } else {
            binding.mapView.mapboxMap.loadStyle(Style.LIGHT) { style ->
                val bitmap = BitmapFactory.decodeResource(resources, R.drawable.red_pin)
                style.addImage("red-pin-icon-id", bitmap)
                mapboxMap = binding.mapView.mapboxMap
                enableLocationComponent()
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
                    addProperty("lat", facility.latitude)
                    addProperty("lon", facility.longitude)
                })
            circleAnnotationManager.create(circleAnnotationOptions)
        }

        var currentDisplayedAnnotation: CircleAnnotation? = null

        circleAnnotationManager.addClickListener(OnCircleAnnotationClickListener { annotation ->
            val data = annotation.getData()
            val facilityName = data?.asJsonObject?.get("name")?.asString
            val facilityType = data?.asJsonObject?.get("type")?.asString
            val facilityLat = data?.asJsonObject?.get("lat")?.asDouble
            val facilityLon = data?.asJsonObject?.get("lon")?.asDouble
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
            val userPoint =
                viewModel.uiState.value.userLocation?.let { Point.fromLngLat(it.second, it.first) }
            val destinationPoint: Point? = facilityLat?.let { lat ->
                facilityLon?.let { lon ->
                    Point.fromLngLat(lon, lat)
                }
            }

            binding.mapView.mapboxMap.loadStyle(Style.LIGHT) { style ->
                if (userPoint != null) {
                    if (destinationPoint != null) {
                        drawRoute(style, userPoint, destinationPoint)
                    }
                }
            }

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

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                checkAndEnableLocation()
            }
        }


    private fun checkPermissions() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                checkAndEnableLocation()
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    private fun checkAndEnableLocation() {
        val locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isLocationEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

        if (isLocationEnabled) {
            enableLocationComponent()
        } else {
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)
        }
    }


    private lateinit var locationComponentPlugin: LocationComponentPlugin

    private fun enableLocationComponent() {
        locationComponentPlugin = binding.mapView.location
        val topImage = ImageHolder.from(R.drawable.user_puck_icon)
        val bearingImage = ImageHolder.from(com.mapbox.navigation.R.drawable.mapbox_user_bearing_icon)
        val shadowImage = ImageHolder.from(com.mapbox.navigation.R.drawable.mapbox_user_stroke_icon)

        locationComponentPlugin.updateSettings {
            enabled = true
            locationPuck = LocationPuck2D(
                topImage = topImage,
                bearingImage = bearingImage,
                shadowImage = shadowImage
            )
            puckBearing = PuckBearing.HEADING
            puckBearingEnabled = true
        }

        locationComponentPlugin.addOnIndicatorPositionChangedListener { point ->
            val currentTime = System.currentTimeMillis()
            if (currentTime - viewModel.uiState.value.lastUpdate >= 5000) {
                val latitude = point.latitude()
                val longitude = point.longitude()
                Log.d("Location", "Lat: $latitude, Long: $longitude")

                viewModel.onLocationChanged(latitude, longitude)
                viewModel.setLastUpdate(currentTime)
            }
        }
    }

    private fun removeRoute(style: Style) {
        if (style.styleSourceExists(routeSourceId)) {
            style.removeStyleSource(routeSourceId)
        }
        if (style.styleLayerExists(routeLayerId)) {
            style.removeStyleLayer(routeLayerId)
        }
    }

    private fun drawRoute(style: Style, origin: Point, destination: Point) {
        removeRoute(style)

        val routeOptions = RouteOptions.builder()
            .coordinatesList(listOf(origin, destination))
            .profile(DirectionsCriteria.PROFILE_DRIVING)
            .overview(DirectionsCriteria.OVERVIEW_FULL)
            .build()

        val client = MapboxDirections.builder()
            .routeOptions(routeOptions)
            .accessToken(getString(R.string.mapbox_access_token))
            .build()

        client.enqueueCall(object : Callback<DirectionsResponse> {
            override fun onResponse(call: Call<DirectionsResponse>, response: Response<DirectionsResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val routes = response.body()!!.routes()
                    if (routes.isNotEmpty()) {
                        val route = routes[0]
                        val geometry = route.geometry()
                        val routeCoordinates = geometry?.let {
                            LineString.fromPolyline(
                                it, 6
                            ).coordinates()
                        }
                        style.addSource(
                            geoJsonSource(routeSourceId) {
                                routeCoordinates?.let { LineString.fromLngLats(it) }
                                    ?.let { geometry(it) }
                            }
                        )
                        style.addLayer(
                            lineLayer(routeLayerId, routeSourceId) {
                                lineColor("blue")
                                lineWidth(5.0)
                            }
                        )
                    } else {
                        viewModel.emitMessage("No routes found")
                    }
                } else {
                    viewModel.emitMessage("Response failed: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<DirectionsResponse>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handler = Handler(Looper.getMainLooper())
        circleAnnotationManager = binding.mapView.annotations.createCircleAnnotationManager()
        handleMapView()
        collectFacilityType()
        initRecyclerView()
        collectUiState()
        collectUiEvents()
        checkPermissions()

    }

    private fun openNoInternetDialog() {
        NoInternetDialogFragment().show(parentFragmentManager, OPEN_NO_INTERNET_DIALOG)
    }
}