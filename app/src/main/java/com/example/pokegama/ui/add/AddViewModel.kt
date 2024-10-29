package com.example.pokegama.ui.add

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokegama.data.model.local.Facility
import com.example.pokegama.data.repo.FacilityRepo
import kotlinx.coroutines.launch
import javax.inject.Inject

class AddViewModel @Inject constructor(
    private val facilityRepo: FacilityRepo
): ViewModel() {
    val facilities: LiveData<List<Facility>> = facilityRepo.getFacility()
}