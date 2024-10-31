package com.example.pokegama.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.pokegama.data.model.local.Facility
import com.example.pokegama.databinding.FacilityRvItemBinding

class FacilitiesAdapter:
    ListAdapter<Facility, FacilitiesAdapter.viewHolder>(DiffCall()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val binding =
            FacilityRvItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return viewHolder(binding)
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    inner class viewHolder(private val binding: FacilityRvItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Facility) {
            binding.facilityItem = data
        }
    }

    class DiffCall : DiffUtil.ItemCallback<Facility>() {
        override fun areItemsTheSame(
            oldItem: Facility,
            newItem: Facility
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: Facility,
            newItem: Facility
        ): Boolean {
            return oldItem == newItem
        }
    }
}