package com.example.pokegama.ui.home

import com.denzcoskun.imageslider.models.SlideModel
import com.example.pokegama.R
import com.example.pokegama.data.model.remote.AdvertisementDTO

data class HomeScreenState(
    val advertisementItems: List<AdvertisementDTO> = emptyList(),
    val imageList: ArrayList<SlideModel> = arrayListOf(SlideModel(R.drawable.ad_placeholder))
)
