package com.example.pokegama.ui.home

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.denzcoskun.imageslider.interfaces.ItemClickListener
import com.denzcoskun.imageslider.models.SlideModel
import com.example.pokegama.R
import com.example.pokegama.databinding.FragmentHomeBinding
import com.example.pokegama.ui.dialogs.NoInternetDialogFragment
import com.example.pokegama.util.OPEN_NO_INTERNET_DIALOG
import com.example.pokegama.util.showToast
import com.example.pokegama.util.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {

    private val binding by viewBinding(FragmentHomeBinding::bind)
    private val viewModel by viewModels<HomeViewModel>()
    private var imageList = ArrayList<SlideModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        collectUiState()
        collectUiEvents()
        binding.buttonNavigateToyagama.setOnClickListener {
            navigateToFacilities("Toyagama")
        }
        binding.buttonNavigateBusUGM.setOnClickListener {
            navigateToFacilities("Bus UGM Stop")
        }
        binding.buttonNavigateSepeda.setOnClickListener {
            navigateToFacilities("Sepeda Kampus")
        }
    }

    private fun navigateToFacilities(facilityType: String) {
        val action = HomeFragmentDirections.actionNavigationHomeToNavigationFacilities(facilityType)
        findNavController().navigate(action)
    }

    private fun selectItem(position: Int){
        val selectedItem = viewModel.uiState.value.imageList[position]
        Log.d("HomeFragment", "Selected Item: $selectedItem")
    }

    private fun collectUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect {
                    imageList = it.imageList
                    Log.d("HomeFragment", "Image List: $imageList")
                    if(imageList.isEmpty()){
                        imageList = arrayListOf(SlideModel(R.drawable.ad_error))
                        binding.adsImageSlider.setImageList(imageList)
                    }
                    else {
                        binding.adsImageSlider.setImageList(imageList)
                        binding.adsImageSlider.setItemClickListener(object : ItemClickListener {
                            override fun onItemSelected(position: Int) {
                                val url = it.advertisementItems
                                    .sortedBy { it.id }
                                    .map { it.redirectLink }
                                    .getOrNull(position)
                                Log.d("HomeFragment", "URL: $url")
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                startActivity(intent)
                            }

                            override fun doubleClick(position: Int) {
                                val url = it.advertisementItems
                                    .sortedBy { it.id }
                                    .map { it.redirectLink }
                                    .getOrNull(position)
                                Log.d("HomeFragment", "URL: $url")
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                startActivity(intent)
                            }
                        })
                    }
                }
            }
        }
    }

    private fun collectUiEvents() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.events.collect { event ->
                    when (event) {
                        HomeScreenEvents.ShowNoInternetDialog -> openNoInternetDialog()
                        is HomeScreenEvents.ShowToast -> {
                            requireContext().showToast(event.message)
                        }
                    }
                }
            }
        }
    }

    private fun openNoInternetDialog() {
        NoInternetDialogFragment().show(parentFragmentManager, OPEN_NO_INTERNET_DIALOG)
    }
}