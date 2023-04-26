package com.example.meteorix

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.meteorix.databinding.ItemMeteoriteBinding

class MeteoriteAdapter(private val onItemClick: (Meteorite) -> Unit): RecyclerView.Adapter<MeteoriteAdapter.MeteoriteViewHolder>() {

    inner class MeteoriteViewHolder(val binding: ItemMeteoriteBinding): RecyclerView.ViewHolder(binding.root), View.OnClickListener {
        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val meteorite = meteorites[adapterPosition]
            onItemClick(meteorite)
        }

    }

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

            holder.itemView.setOnClickListener {
                onItemClick(meteorite)
            }
        }
    }


    override fun getItemCount() = meteorites.size
}