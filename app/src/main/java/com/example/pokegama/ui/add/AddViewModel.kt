package com.example.pokegama.ui.add

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokegama.data.model.Facility
import com.example.pokegama.data.repo.FacilityRepo
import kotlinx.coroutines.launch

class AddViewModel @inject constructor(
    private val facilityRepo: FacilityRepo
): ViewModel() {
    annotation class inject
    val facilities: LiveData<List<Facility>> = facilityRepo.getFacility()
}