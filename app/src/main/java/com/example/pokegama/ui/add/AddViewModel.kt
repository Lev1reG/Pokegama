package com.example.pokegama.ui.add

import android.content.ContentResolver
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cloudinary.Cloudinary
import com.cloudinary.utils.ObjectUtils
import com.example.pokegama.BuildConfig
import com.example.pokegama.data.model.remote.FacilityDTO
import com.example.pokegama.data.repo.FacilityRepo
import com.example.pokegama.util.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class AddViewModel @Inject constructor(
    private val facilityRepo: FacilityRepo
) : ViewModel() {
    private val _uiState = MutableStateFlow(AddScreenState())
    val uiState: StateFlow<AddScreenState> = _uiState

    private val _events = MutableSharedFlow<AddScreenEvents>()
    val events = _events.asSharedFlow()

    private lateinit var cloudinary: Cloudinary

    private fun setupCloudinary() {
        val config = mapOf(
            "cloud_name" to BuildConfig.CLOUDINARY_NAME,
            "api_key" to BuildConfig.CLOUDINARY_API_KEY,
            "api_secret" to BuildConfig.CLOUDINARY_API_SECRET
        )
        cloudinary = Cloudinary(config)
    }

    suspend fun uploadImageToCloudinary(contentResolver: ContentResolver, fileUri: Uri): Boolean {
        setupCloudinary()
        val dateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
        val dateNow = dateFormat.format(Date())
        val publicId = "${_uiState.value.type}_${_uiState.value.name}_$dateNow"

        return withContext(Dispatchers.IO) {
            try {
                val inputStream = contentResolver.openInputStream(fileUri)
                _uiState.emit(_uiState.value.copy(isLoading = true))
                val uploadResult = cloudinary.uploader()
                    .upload(inputStream, ObjectUtils.asMap("public_id", publicId))
                _uiState.emit(_uiState.value.copy(isLoading = false))
                val imageUrl = uploadResult["url"] as String
                setFacilityImg(imageUrl)
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }

    fun addFacility() = viewModelScope.launch {
        _uiState.emit(_uiState.value.copy(isLoading = true))
        val facility = FacilityDTO(
            type = _uiState.value.type,
            name = _uiState.value.name,
            faculty = if (_uiState.value.faculty == "LAIN-LAIN") "UGM" else _uiState.value.faculty,
            facilityImg = _uiState.value.facilityImg,
            latitude = _uiState.value.latitude,
            longitude = _uiState.value.longitude,
            description = _uiState.value.description,
            isAccepted = _uiState.value.isAccepted
        )
        val resource = facilityRepo.insertFacilityToDatabase(facility)
        _uiState.emit(_uiState.value.copy(isLoading = false))
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

    fun setLatitude(latitude: Double) = viewModelScope.launch {
        _uiState.emit(_uiState.value.copy(latitude = latitude))
    }

    fun setLongitude(longitude: Double) = viewModelScope.launch {
        _uiState.emit(_uiState.value.copy(longitude = longitude))
    }

    private fun handleError(resource: Resource.Error<Unit>) = viewModelScope.launch {
        val event = when (resource.errorType) {
            ERROR_TYPE.NO_INTERNET -> AddScreenEvents.ShowNoInternetDialog
            ERROR_TYPE.UNKNOWN -> AddScreenEvents.ShowToast(resource.message)
        }
        _events.emit(event)
    }
}