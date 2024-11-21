package com.example.pokegama.data.remote

import com.example.pokegama.data.model.remote.AdvertisementDTO
import com.example.pokegama.util.Resource

interface AdvertisementDataSource {
    suspend fun fetchAllAdvertisement(): Resource<List<AdvertisementDTO>>
}