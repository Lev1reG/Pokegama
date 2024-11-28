package com.example.pokegama.ui.facilities

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokegama.data.repo.FacilityRepo
import com.example.pokegama.ui.add.AddScreenEvents
import com.example.pokegama.util.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FacilitiesViewModel @Inject constructor(
    private val facilityRepo: FacilityRepo
) : ViewModel() {
    private val _uiState = MutableStateFlow(FacilitiesScreenState())
    val uiState = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<FacilitiesScreenEvents>()
    val events = _events.asSharedFlow()

    val UGMCoord = Pair(-7.77083, 110.37762)

    init {
        viewModelScope.launch {
            loadFacility()
            collectFacility()
        }
    }

    fun onLocationChanged(lat: Double, lon: Double) {
        updateUserLocation(lat, lon)
        if(haversine(lat, lon, UGMCoord.first, UGMCoord.second) > 3000)
            emitMessage("Anda terlalu jauh dari UGM")
    }

    private fun updateUserLocation(lat: Double, lon: Double) {
        val currentState = _uiState.value
        _uiState.value = currentState.copy(userLocation = Pair(lat, lon))

        // Trigger re-sorting of facilities if facilityItems are already loaded
        if (currentState.facilityItems.isNotEmpty()) {
            sortFacilitiesByDistance()
        }
    }

    private fun sortFacilitiesByDistance() {
        _uiState.value.userLocation?.let { (userLat, userLon) ->
            val sortedFacilities = _uiState.value.facilityItems.map { facility ->
                val distance = haversine(userLat, userLon, facility.latitude, facility.longitude)
                facility.copy(distance = distance)
            }.sortedBy { it.distance }
            _uiState.value = _uiState.value.copy(facilityItems = sortedFacilities)
        }
    }


    private fun collectFacility() = viewModelScope.launch {
        uiState.value.facilityType?.let { it ->
            facilityRepo.getFacilityOfType(it).collect { facilities ->
                val userLocation = _uiState.value.userLocation
                Log.d("FacilitiesViewModel", "Facilities collected: $facilities")
                val facilitiesWithDistance = userLocation?.let { (userLat, userLon) ->
                    facilities.map { facility ->
                        val distance = haversine(userLat, userLon, facility.latitude, facility.longitude)
                        facility.copy(distance = distance)
                    }.sortedBy { it.distance }
                } ?: facilities
                _uiState.value = _uiState.value.copy(facilityItems = facilitiesWithDistance)
            }
        }
    }

    private suspend fun loadFacility() {
        Log.d("FacilitiesViewModel", "Fetching facility data...")
        _uiState.emit(_uiState.value.copy(isLoading = true))
        val resource = facilityRepo.fetchFacility()
        _uiState.emit(_uiState.value.copy(isLoading = false))
        if (resource is Resource.Error) {
            Log.e("FacilitiesViewModel", "Error fetching facilities: ${resource.message}")
            handleError(resource)
        } else {
            Log.d("FacilitiesViewModel", "Facility data fetched successfully")
        }
    }


    fun setFacilityTypeAndLoad(type: String) {
        viewModelScope.launch {
            _uiState.emit(_uiState.value.copy(facilityType = type))
        }
    }

    fun setLastUpdate(type: Long){
        viewModelScope.launch {
            _uiState.emit(_uiState.value.copy(lastUpdate = type))
        }
    }

    fun emitMessage(message: String) = viewModelScope.launch {
        val event = FacilitiesScreenEvents.ShowToast(message)
        _events.emit(event)
    }

    private fun handleError(resource: Resource.Error<*>) = viewModelScope.launch {
        val event = when (resource.errorType) {
            ERROR_TYPE.NO_INTERNET -> FacilitiesScreenEvents.ShowNoInternetDialog
            ERROR_TYPE.UNKNOWN -> FacilitiesScreenEvents.ShowToast(resource.message)
        }
        _events.emit(event)
    }
}