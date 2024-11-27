package com.example.pokegama.data.repo

import android.util.Log
import com.example.pokegama.data.local.dataSource.RoomFacilityDataSource
import com.example.pokegama.data.model.local.Facility
import com.example.pokegama.data.model.mapper.FacilityMapper
import com.example.pokegama.data.model.remote.FacilityDTO
import com.example.pokegama.data.remote.FirestoreFacilityDataSource
import com.example.pokegama.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

class FacilityRepo @Inject constructor(
    private val facilityDataSource: RoomFacilityDataSource,
    private val firestoreFacilityDataSource: FirestoreFacilityDataSource,
    private val facilityItemMapper: FacilityMapper
) {
    fun getFacility() = facilityDataSource.getFacility()
    fun getFacilityOfType(facilityType: String) = facilityDataSource.getFacilityOfType(facilityType)

    suspend fun fetchFacilityOfType(facilityType: String): Resource<Unit> =
        withContext(Dispatchers.IO) {
            val resource = firestoreFacilityDataSource.fetchAllFacilitiesOfType(facilityType)
            return@withContext if (resource is Resource.Success && resource.data != null) {
                dumpAllFacilitiesIntoDB(facilityItemMapper.toEntityList(resource.data!!))
                Resource.Success()
            } else {
                Resource.Error(errorType = resource.errorType, message = "Cannot load facility")
            }
        }

    suspend fun insertFacilityToDatabase(facilityDTO: FacilityDTO): Resource<Unit> =
        withContext(Dispatchers.IO) {
            val resource = firestoreFacilityDataSource.addFacility(facilityDTO)
            return@withContext if (resource is Resource.Success) {
                Resource.Success()
            } else {
                Resource.Error(errorType = resource.errorType, message = "Cannot insert facility")
            }
        }

    suspend fun fetchFacility(): Resource<Unit> = withContext(Dispatchers.IO) {
        val resource = firestoreFacilityDataSource.fetchAllAcceptedFacilities()
        return@withContext if (resource is Resource.Success && resource.data != null) {
            dumpAllFacilitiesIntoDB(facilityItemMapper.toEntityList(resource.data!!))
            Resource.Success()
        } else {
            Resource.Error(errorType = resource.errorType, message = "Cannot load facility")
        }
    }

    private suspend fun dumpAllFacilitiesIntoDB(facilityItems: List<Facility>) {
        deleteAllFromFacilityDB()
        //TODO : Implement Jarak here
        val facility = facilityItems
        facilityDataSource.insertAllFacility(facility)
    }

    private suspend fun deleteAllFromFacilityDB() {
        facilityDataSource.deleteAll()
    }
}
