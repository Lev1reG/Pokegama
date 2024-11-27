package com.example.pokegama.ui.add

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokegama.data.model.remote.FacilityDTO
import com.example.pokegama.data.repo.FacilityRepo
import com.example.pokegama.util.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddViewModel @Inject constructor(
    private val facilityRepo: FacilityRepo
): ViewModel() {
    private val _uiState = MutableStateFlow(AddScreenState())
    val uiState: StateFlow<AddScreenState> = _uiState

    private val _events = MutableSharedFlow<AddScreenEvents>()
    val events = _events.asSharedFlow()

    fun addFacility() = viewModelScope.launch {
        val facility = FacilityDTO(
            type = _uiState.value.type,
            name = _uiState.value.name,
            faculty = _uiState.value.faculty,
            facilityImg = _uiState.value.facilityImg,
            latitude = _uiState.value.latitude,
            longitude = _uiState.value.longitude,
            description = _uiState.value.description,
            isAccepted = _uiState.value.isAccepted
        )
        val resource = facilityRepo.insertFacilityToDatabase(facility)
        if (resource is Resource.Error) {
            Log.e("AddViewModel", "Error insert facility: ${resource.message}")
            handleError(resource)
        } else {
            val event = AddScreenEvents.ShowToast("Fasilitas Diusulkan")
            _events.emit(event)
            Log.d("AddViewModel", "Facility data inserted successfully")
        }
    }

    fun emitMessage(message: String) = viewModelScope.launch {
        val event = AddScreenEvents.ShowToast(message)
        _events.emit(event)
    }

    fun onTypeChange(type: String) = viewModelScope.launch {
        _uiState.emit(_uiState.value.copy(type = type))
    }

    fun onNameChange(name: String) = viewModelScope.launch {
        _uiState.emit(_uiState.value.copy(name = name))
    }

    fun onFacultyChange(faculty: String) = viewModelScope.launch {
        _uiState.emit(_uiState.value.copy(faculty = faculty))
    }

    fun onDescriptionChange(description: String) = viewModelScope.launch {
        _uiState.emit(_uiState.value.copy(description = description))
    }

    fun setFileUri(fileUri: Uri) = viewModelScope.launch {
        _uiState.emit(_uiState.value.copy(fileUri = fileUri))
    }

    fun setFacilityImg(facilityImg: String) = viewModelScope.launch {
        _uiState.emit(_uiState.value.copy(facilityImg = facilityImg))
    }

    fun setFacilityImgName(facilityImgName: String) = viewModelScope.launch {
        _uiState.emit(_uiState.value.copy(facilityImgName = facilityImgName))
    }

    private fun handleError(resource: Resource.Error<Unit>) = viewModelScope.launch {
        val event = when (resource.errorType) {
            ERROR_TYPE.NO_INTERNET -> AddScreenEvents.ShowNoInternetDialog
            ERROR_TYPE.UNKNOWN -> AddScreenEvents.ShowToast(resource.message)
        }
        _events.emit(event)
    }
}