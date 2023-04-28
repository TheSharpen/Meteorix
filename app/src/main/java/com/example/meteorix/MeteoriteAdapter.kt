package com.example.meteorix

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.meteorix.databinding.ItemMeteoriteBinding
import java.util.*


class MeteoriteAdapter(private val onItemClick: (Meteorite) -> Unit) :
    RecyclerView.Adapter<MeteoriteAdapter.MeteoriteViewHolder>() {

    private val diffCallback = object : DiffUtil.ItemCallback<Meteorite>() {
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
        set(value) {
            differ.submitList(value)
        }

    private var meteoritesFiltered: List<Meteorite> = meteorites

    fun filter(string: String?): List<Meteorite> {
        try {
            if (string == null || string.isEmpty() || string == "") {
                meteoritesFiltered = meteorites
            } else {
                val filterPattern = string.trim().lowercase(Locale.ROOT)
                meteoritesFiltered = meteorites.filter {
                    it.name.lowercase(Locale.ROOT).contains(filterPattern)
                }
            }
        } catch (e: Exception) {
            Log.d("XLOG", "exception ${e.message}")
            return emptyList()
        }

        notifyDataSetChanged()
        return meteoritesFiltered
    }

    inner class MeteoriteViewHolder(val binding: ItemMeteoriteBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {
        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val meteorite = meteorites[adapterPosition]
            onItemClick(meteorite)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MeteoriteViewHolder {
        return MeteoriteViewHolder(
                ItemMeteoriteBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                )
        )
    }

    override fun onBindViewHolder(holder: MeteoriteViewHolder, position: Int) {
        val meteorite = if (meteorites.isEmpty()) null else meteorites[position]
        if (meteorite != null) {
            holder.binding.apply {
                tvMeteoriteName.text = meteorite.name
                holder.itemView.setOnClickListener {
                    onItemClick.invoke(meteorite)
                }
            }
        }
    }

    override fun getItemCount() = meteorites.size
}
