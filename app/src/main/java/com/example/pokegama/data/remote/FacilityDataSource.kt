package com.example.pokegama.data.remote

import com.example.pokegama.data.model.remote.FacilityDTO
import com.example.pokegama.util.Resource

interface FacilityDataSource {
    suspend fun fetchAllFacilities(): Resource<List<FacilityDTO>>

    suspend fun fetchAllFacilitiesOfType(facilityType: String): Resource<List<FacilityDTO>>

    suspend fun fetchAllAcceptedFacilities(): Resource<List<FacilityDTO>>

    suspend fun addFacility(facilityDTO: FacilityDTO): Resource<FacilityDTO>
}