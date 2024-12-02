package com.example.pokegama.ui.adapter

import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.pokegama.data.model.local.Facility
import com.example.pokegama.databinding.FacilityRvItemBinding
import com.example.pokegama.util.*

class FacilitiesAdapter:
    ListAdapter<Facility, FacilitiesAdapter.viewHolder>(DiffCall()) {

    var onItemClick: ((Facility) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val binding =
            FacilityRvItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return viewHolder(binding)
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        val item = getItem(position)
        onItemClick?.let { holder.bind(item, it) }
    }

    inner class viewHolder(private val binding: FacilityRvItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Facility, onItemClick: (Facility) -> Unit) {
            binding.facilityItem = data
            val backgroundColor = getBackgroundColor(binding.root.context, data.faculty)
            if(data.faculty == "UGM"){
                binding.facultyName.textSize = 15f
            } else{
                binding.facultyName.textSize = 18f
            }
            (binding.facultyName.background as GradientDrawable).setColor(backgroundColor)

            binding.root.setOnClickListener {
                onItemClick(data)
            }
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