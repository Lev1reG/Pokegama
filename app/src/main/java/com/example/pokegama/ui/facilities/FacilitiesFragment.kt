package com.example.pokegama.ui.facilities

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.pokegama.R
import com.example.pokegama.databinding.FragmentAddBinding
import com.example.pokegama.databinding.FragmentFacilitiesBinding

class FacilitiesFragment : Fragment() {
    private var facilityType: String? = null

    private var _binding: FragmentFacilitiesBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            facilityType = it.getString("facility_type")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFacilitiesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.Title.text = facilityType
    }
}