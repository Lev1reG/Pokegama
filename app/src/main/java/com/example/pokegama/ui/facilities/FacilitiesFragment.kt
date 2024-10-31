package com.example.pokegama.ui.facilities

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.*
import com.example.pokegama.R
import com.example.pokegama.databinding.FragmentFacilitiesBinding
import com.example.pokegama.ui.adapter.FacilitiesAdapter
import com.example.pokegama.ui.dialogs.NoInternetDialogFragment
import com.example.pokegama.util.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FacilitiesFragment : Fragment(R.layout.fragment_facilities) {
    private var facilityType: String? = null
    private val binding by viewBinding(FragmentFacilitiesBinding::bind)
    private val viewModel by viewModels<FacilitiesViewModel>()
    private val facilityAdapter = FacilitiesAdapter()

    private fun collectFacilityType() {
        arguments?.let {
            facilityType = it.getString("facility_type")
            viewModel.setFacilityTypeAndLoad(facilityType ?: "")
            Log.d("FacilitiesFragment", "Facility Type: $facilityType")
            binding.titleFacilities.text = facilityType
        }
    }

    private fun collectUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect {
                    facilityAdapter.submitList(it.facilityItems)
                }
            }
        }
    }

    private fun collectUiEvents() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.events.collect { event ->
                    when (event) {
                        FacilitiesScreenEvents.ShowNoInternetDialog -> openNoInternetDialog()
                        is FacilitiesScreenEvents.ShowToast -> {
                            requireContext().showToast(event.message)
                        }
                    }
                }
            }
        }
    }

    private fun initRecyclerView() {
        binding.facilityRv.apply {
            adapter = facilityAdapter
            setHasFixedSize(false)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        collectFacilityType()
        initRecyclerView()
        collectUiState()
        collectUiEvents()
    }

    private fun openNoInternetDialog() {
        NoInternetDialogFragment().show(parentFragmentManager, OPEN_NO_INTERNET_DIALOG)
    }
}