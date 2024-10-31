package com.example.pokegama.ui.facilities

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokegama.data.repo.FacilityRepo
import com.example.pokegama.util.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FacilitiesViewModel @Inject constructor(
    private val facilityRepo: FacilityRepo
) : ViewModel(){
    private val _uiState = MutableStateFlow(FacilitiesScreenState())
    val uiState = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<FacilitiesScreenEvents>()
    val events = _events.asSharedFlow()

    private fun collectFacility() = viewModelScope.launch {
        facilityRepo.getFacility().collect { facilities ->
            Log.d("FacilitiesViewModel", "Facilities collected: $facilities")
            _uiState.value = _uiState.value.copy(facilityItems = facilities)
        }
    }

    private suspend fun loadFacility(facilityType: String){
        Log.d("FacilitiesViewModel", "Fetching facility data...")
        val resource = facilityRepo.fetchFacilityOfType(facilityType)
        if (resource is Resource.Error) {
            Log.e("FacilitiesViewModel", "Error fetching facilities: ${resource.message}")
            handleError(resource)
        } else {
            Log.d("FacilitiesViewModel", "Facility data fetched successfully")
            collectFacility()
        }
    }


    fun setFacilityTypeAndLoad(type: String) {
        viewModelScope.launch {
            loadFacility(type)
        }
    }

    private fun handleError(resource: Resource.Error<*>) = viewModelScope.launch {
        val event = when (resource.errorType) {
            ERROR_TYPE.NO_INTERNET -> FacilitiesScreenEvents.ShowNoInternetDialog
            ERROR_TYPE.UNKNOWN -> FacilitiesScreenEvents.ShowToast(resource.message)
        }
        _events.emit(event)
    }
}