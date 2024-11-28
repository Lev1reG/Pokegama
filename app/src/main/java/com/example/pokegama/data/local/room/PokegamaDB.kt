package com.example.pokegama.data.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.pokegama.data.model.local.Facility

@Database(
    entities = [Facility::class],
    version = 7,
    exportSchema = false
)
abstract class PokegamaDB : RoomDatabase() {
    abstract fun getFacilityDao(): FacilityDao
}
