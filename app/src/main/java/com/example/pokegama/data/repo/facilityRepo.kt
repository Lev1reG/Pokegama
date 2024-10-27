package com.example.pokegama.data.repo

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.pokegama.data.model.Facility
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class FacilityRepo @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    fun getFacility(): LiveData<List<Facility>> {
        val liveData = MutableLiveData<List<Facility>>()
        firestore.collection("facilities").get()
            .addOnSuccessListener { documents ->
                val facilityList = documents.map { document ->
                    document.toObject(Facility::class.java)
                }
                liveData.value = facilityList
            }
            .addOnFailureListener { exception ->
                Log.e("FacilityRepo", "Error getting facilities: ", exception)
                liveData.value = emptyList()
            }
        return liveData
    }
}
