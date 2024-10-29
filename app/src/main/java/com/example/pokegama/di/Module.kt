package com.example.pokegama.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.example.pokegama.data.local.room.FacilityDao
import com.example.pokegama.data.local.room.PokegamaDB
import com.example.pokegama.data.model.mapper.FacilityMapper
import com.example.pokegama.util.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

//Dependencies Injection using dagger hilt
@Module
@InstallIn(SingletonComponent::class)
object Module {
    @Provides
    fun providesFirestore(): FirebaseFirestore = Firebase.firestore

    @Provides
    @Singleton
    fun providesRoomDb(@ApplicationContext context: Context): PokegamaDB =
        Room.databaseBuilder(context, PokegamaDB::class.java, "Pokegama_DB")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun providesFacilityDao(roomDatabase: PokegamaDB): FacilityDao = roomDatabase.getFacilityDao()

    @Provides
    fun providesFacilityMapper(): FacilityMapper = FacilityMapper()

    @Provides
    fun providesInternetChecker(@ApplicationContext context: Context): InternetChecker = InternetChecker(context)
}