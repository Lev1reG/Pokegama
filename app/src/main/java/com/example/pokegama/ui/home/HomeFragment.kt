package com.example.pokegama.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.pokegama.R
import com.example.pokegama.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    private lateinit var buttonNavigateToyagama: ImageButton
    private lateinit var buttonNavigateBusUGM: ImageButton
    private lateinit var buttonNavigateSepeda: ImageButton

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        buttonNavigateToyagama = view.findViewById(R.id.buttonNavigateToyagama)
        buttonNavigateBusUGM = view.findViewById(R.id.buttonNavigateBusUGM)
        buttonNavigateSepeda = view.findViewById(R.id.buttonNavigateSepeda)

        buttonNavigateToyagama.setOnClickListener {
            navigateToFacilities("Toyagama")
        }

        buttonNavigateBusUGM.setOnClickListener {
            navigateToFacilities("Bus UGM Stop")
        }

        buttonNavigateSepeda.setOnClickListener {
            navigateToFacilities("Sepeda Kampus")
        }
    }

    private fun navigateToFacilities(facilityType: String) {
        val action = HomeFragmentDirections.actionNavigationHomeToNavigationFacilities(facilityType)
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}