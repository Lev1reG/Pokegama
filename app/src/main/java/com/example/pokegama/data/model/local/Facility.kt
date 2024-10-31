package com.example.pokegama.data.model.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.pokegama.util.*

@Entity(tableName = "facility_table")
data class Facility(
    val type: String,
    val name: String,
    val faculty: String,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val description: String? = null,
    var isAccepted: Boolean = false,
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
)
