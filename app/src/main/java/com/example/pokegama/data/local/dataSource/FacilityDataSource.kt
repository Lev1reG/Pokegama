package com.example.pokegama.data.local.dataSource

import com.example.pokegama.data.local.room.FacilityDao
import com.example.pokegama.data.model.local.Facility
import javax.inject.Inject

class FacilityDataSource @Inject constructor(private val facilityDao: FacilityDao) {

    fun getFacility() = facilityDao.getFacility()

    suspend fun insertAllFacility(items: List<Facility>) = facilityDao.insertAllFacility(items)

    suspend fun deleteAll() = facilityDao.deleteAll()

}