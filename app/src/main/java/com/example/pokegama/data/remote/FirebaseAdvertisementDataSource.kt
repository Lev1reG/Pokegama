package com.example.pokegama.data.remote

import com.example.pokegama.data.model.remote.AdvertisementDTO
import com.example.pokegama.util.*
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirestoreAdvertisementDataSource @Inject constructor(
    private val fireStore: FirebaseFirestore,
    private val internetChecker: InternetChecker
): AdvertisementDataSource {
    override suspend fun fetchAllAdvertisement() = try {
        if (internetChecker.hasInternetConnection()) {
            val advertisements = fireStore.collection(ADVERTISEMENT_COLLECTION).get().await().toObjects(AdvertisementDTO::class.java)
            Resource.Success(advertisements)
        } else
            Resource.Error(
                errorType = ERROR_TYPE.NO_INTERNET,
                message = NO_INTERNET_MESSAGE
            )
    } catch (e: Exception) {
        Resource.Error(e.message.toString())
    }
}