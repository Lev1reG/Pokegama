package com.example.pokegama.data.local.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.pokegama.data.model.local.Facility
import kotlinx.coroutines.flow.Flow

@Dao
interface FacilityDao {

    @Insert
    suspend fun insertAllFacility(facilityItem: List<Facility>)

    @Query("SELECT * FROM facility_table")
    fun getFacility(): Flow<List<Facility>>

    @Query("DELETE FROM facility_table")
    suspend fun deleteAll()
}
