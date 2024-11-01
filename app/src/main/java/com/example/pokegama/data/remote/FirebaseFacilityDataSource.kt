package com.example.pokegama.data.remote

import com.example.pokegama.data.model.remote.FacilityDTO
import com.example.pokegama.util.*
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirestoreFacilityDataSource @Inject constructor(
    private val fireStore: FirebaseFirestore,
    private val internetChecker: InternetChecker
): FacilityDataSource {
    override suspend fun fetchAllFacilities() = try {
        if (internetChecker.hasInternetConnection()) {
            val users = fireStore.collection(FACILITY_COLLECTION).get().await().toObjects(FacilityDTO::class.java)
            Resource.Success(users)
        } else
            Resource.Error(
                errorType = ERROR_TYPE.NO_INTERNET,
                message = NO_INTERNET_MESSAGE
            )
    } catch (e: Exception) {
        Resource.Error(e.message.toString())
    }

    override suspend fun fetchAllFacilitiesOfType(facilityType: String) = try {
        if (internetChecker.hasInternetConnection()) {
            val users = fireStore.collection(FACILITY_COLLECTION).whereEqualTo("type", facilityType).whereEqualTo("accepted", true).get().await().toObjects(FacilityDTO::class.java)
            Resource.Success(users)
        } else
            Resource.Error(
                errorType = ERROR_TYPE.NO_INTERNET,
                message = NO_INTERNET_MESSAGE
            )
    } catch (e: Exception) {
        Resource.Error(e.message.toString())
    }

    override suspend fun fetchAllAcceptedFacilities() = try {
        if (internetChecker.hasInternetConnection()) {
            val acceptedFacilities = fireStore.collection(FACILITY_COLLECTION).whereEqualTo("accepted", true).get().await().toObjects(FacilityDTO::class.java)
            Resource.Success(acceptedFacilities)
        } else {
            Resource.Error(
                errorType = ERROR_TYPE.NO_INTERNET,
                message = NO_INTERNET_MESSAGE
            )
        }
    } catch (e: Exception) {
        Resource.Error(e.message.toString())
    }

    override suspend fun addFacility(facilityDTO: FacilityDTO): Resource<FacilityDTO> = try {
        if (internetChecker.hasInternetConnection()) {
            fireStore.collection(FACILITY_COLLECTION).add(facilityDTO).await()
            Resource.Success(facilityDTO)
        } else
            Resource.Error(
                errorType = ERROR_TYPE.NO_INTERNET,
                message = NO_INTERNET_MESSAGE
            )
    } catch (e: Exception) {
        Resource.Error(e.message.toString())
    }
}