package com.example.meteorix

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.meteorix.databinding.ItemMeteoriteBinding

class MeteoriteAdapter: RecyclerView.Adapter<MeteoriteAdapter.MeteoriteViewHolder>() {

    inner class MeteoriteViewHolder(val binding: ItemMeteoriteBinding): RecyclerView.ViewHolder(binding.root)

    private val diffCallback = object: DiffUtil.ItemCallback<Meteorite>() {
        override fun areItemsTheSame(oldItem: Meteorite, newItem: Meteorite): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Meteorite, newItem: Meteorite): Boolean {
           return oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(this, diffCallback)

    var meteorites: List<Meteorite>
    get() = differ.currentList
    set(value) {differ.submitList(value)}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MeteoriteViewHolder {
        return MeteoriteViewHolder(ItemMeteoriteBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
        ))
    }

    override fun onBindViewHolder(holder: MeteoriteViewHolder, position: Int) {
        holder.binding.apply {
            val meteorite = meteorites[position]
            tvMeteoriteName.text = meteorite.name
        }
    }

    override fun getItemCount() = meteorites.size
}