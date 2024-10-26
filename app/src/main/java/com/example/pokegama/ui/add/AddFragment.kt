package com.example.pokegama.ui.add

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
import com.example.pokegama.R
import com.example.pokegama.databinding.FragmentAddBinding

class AddFragment : Fragment() {

    companion object {
        fun newInstance() = AddFragment()
        val IMAGE_REQUEST_CODE = 100
    }

    private val viewModel: AddViewModel by viewModels()
    private var _binding: FragmentAddBinding? = null
    private val binding get() = _binding!!
    private lateinit var button: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // TODO: Use the ViewModel
    }

    override fun onResume(){
        super.onResume()
        val facility_list = resources.getStringArray(R.array.facility_list)
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, facility_list)
        binding.addfacilityFacilityAutoComplete.setAdapter(arrayAdapter)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        button = view.findViewById(R.id.addfacility_facilityphoto_button)

        button.setOnClickListener{
            pickImageGallery()
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