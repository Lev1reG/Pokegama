package com.example.pokegama.data.repo

import com.example.pokegama.data.local.dataSource.RoomFacilityDataSource
import com.example.pokegama.data.model.mapper.FacilityMapper
import com.example.pokegama.data.remote.AdvertisementDataSource
import com.example.pokegama.data.remote.FirestoreAdvertisementDataSource
import com.example.pokegama.data.remote.FirestoreFacilityDataSource
import javax.inject.Inject

class AdvertisementRepo @Inject constructor(
    private val firestoreAdvertisementDataSource: FirestoreAdvertisementDataSource,
) {
    suspend fun getAdvertisement() = firestoreAdvertisementDataSource.fetchAllAdvertisement()

}