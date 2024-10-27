package com.example.pokegama.ui.add

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Intent
import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Toast
import com.example.pokegama.R
import com.example.pokegama.data.model.Facility
import com.example.pokegama.databinding.FragmentAddBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AddFragment : Fragment() {

    companion object {
        fun newInstance() = AddFragment()
        val IMAGE_REQUEST_CODE = 100
    }

    private val viewModel: AddViewModel by viewModels()
    private var _binding: FragmentAddBinding? = null
    private val binding get() = _binding!!
    private val db = Firebase.firestore
    private lateinit var button: Button
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // TODO: Use the ViewModel
    }

    override fun onResume(){
        super.onResume()
        val facility_list = resources.getStringArray(R.array.facility_list)
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, facility_list)
        binding.addfacilityFacilityAutoComplete.setAdapter(arrayAdapter)

        val faculty_list = resources.getStringArray(R.array.faculty_list)
        val facultyArrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, faculty_list)
        binding.addfacilityFacultyAutoComplete.setAdapter(facultyArrayAdapter)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        button = view.findViewById(R.id.addfacility_facilityphoto_button)
        button.setOnClickListener{
            pickImageGallery()
        }

        binding.addfacilitySubmit.setOnClickListener{
            val _facility = binding.addfacilityFacilityAutoComplete.text.toString()
            val _name = binding.addfacilityNameTextView.text.toString()
            val _faculty = binding.addfacilityFacultyAutoComplete.text.toString()
            val _imageUri = binding.addfacilityFacilityphotoHint.text.toString()
            val _description = binding.addfacilityDescriptionTextView.text.toString()

            databaseReference = FirebaseDatabase.getInstance().getReference("Facilities Information")
            val _facilityId = databaseReference.push().key!!
            val facilityData = Facility(id = _facilityId, type = _facility, faculty = _faculty, name = _name, imageUri = _imageUri, description = _description)
            db.collection("facilities").document(_facilityId).set(facilityData).addOnSuccessListener {
                binding.addfacilityNameTextView.text.clear()
                binding.addfacilityFacilityAutoComplete.text.clear()
                binding.addfacilityFacultyAutoComplete.text.clear()
                binding.addfacilityFacilityphotoHint.text = "Upload Gambar(JPG, PNG)"
                binding.addfacilityDescriptionTextView.text.clear()

                Toast.makeText(this.context, "Saved", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener{
                Toast.makeText(this.context, "Failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun pickImageGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode : Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_REQUEST_CODE && resultCode == RESULT_OK) {
            binding.addfacilityFacilityphotoHint.text = data?.data.toString()

        }

    }
}