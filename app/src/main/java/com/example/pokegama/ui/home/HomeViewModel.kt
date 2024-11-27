package com.example.pokegama.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.denzcoskun.imageslider.models.SlideModel
import com.example.pokegama.data.repo.AdvertisementRepo
import com.example.pokegama.ui.add.AddScreenEvents
import com.example.pokegama.util.ERROR_TYPE
import com.example.pokegama.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val advertisementRepo: AdvertisementRepo
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeScreenState())
    val uiState = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<HomeScreenEvents>(replay = 1)
    val events = _events.asSharedFlow()

    init {
        viewModelScope.launch {
            collectAdvertisement()
        }
    }

    private fun collectAdvertisement() = viewModelScope.launch {
        val result = advertisementRepo.getAdvertisement()
        if (result is Resource.Error) {
            Log.e("HomeViewModel", "Error fetching facilities: ${result.message}")
            handleError(result)
        } else {
            Log.d("HomeViewModel", "Facility data fetched successfully")
        }
        Log.d("HomeViewModel", "Advertisements collected: ${result.data}")
        result.data?.let { uiState.value.copy(advertisementItems = it) }?.let { _uiState.emit(it) }
        Log.d("HomeViewModel", "Advertisements emitted: ${uiState.value.advertisementItems}")

        val advertisementsImg = result.data
            ?.sortedBy { it.id }
            ?.map { SlideModel(it.advertisementImg) }
            ?: emptyList()
        _uiState.value = _uiState.value.copy(imageList = ArrayList(advertisementsImg))

        Log.d("HomeViewModel", "Advertisements emitted: ${_uiState.value.imageList}")
    }

    private fun handleError(resource: Resource.Error<*>) = viewModelScope.launch {
        val event = when (resource.errorType) {
            ERROR_TYPE.NO_INTERNET -> HomeScreenEvents.ShowToast(resource.message)
            ERROR_TYPE.UNKNOWN -> HomeScreenEvents.ShowToast(resource.message)
        }
        _events.emit(event)
    }
}