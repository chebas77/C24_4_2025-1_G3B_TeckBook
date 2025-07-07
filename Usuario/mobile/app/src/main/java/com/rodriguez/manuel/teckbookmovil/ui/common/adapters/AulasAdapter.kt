package com.rodriguez.manuel.teckbookmovil.ui.common.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.rodriguez.manuel.teckbookmovil.data.models.aula.AulaVirtual
import com.rodriguez.manuel.teckbookmovil.databinding.ItemAulaBinding

class AulasAdapter : ListAdapter<AulaVirtual, AulasAdapter.AulaViewHolder>(AulaDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AulaViewHolder {
        val binding = ItemAulaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AulaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AulaViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class AulaViewHolder(private val binding: ItemAulaBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: AulaVirtual) {
            binding.textViewAulaName.text = item.getTituloOrNombre()
            // Ejemplo extra:
            // binding.textViewProfesorName.text = item.getProfesorInfo()
        }
    }

    class AulaDiffCallback : DiffUtil.ItemCallback<AulaVirtual>() {
        override fun areItemsTheSame(oldItem: AulaVirtual, newItem: AulaVirtual) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: AulaVirtual, newItem: AulaVirtual) = oldItem == newItem
    }
}


