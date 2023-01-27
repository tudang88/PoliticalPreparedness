package com.example.android.politicalpreparedness.election.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.politicalpreparedness.databinding.ElectionsListItemBinding
import com.example.android.politicalpreparedness.network.models.Election

/**
 * RecyclerViewAdapter used for two recyclerView on ElectionFragment
 */
class ElectionListAdapter(private val clickListener: ElectionListener) :
    ListAdapter<Election, ElectionViewHolder>(ElectionDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ElectionViewHolder {
        return ElectionViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ElectionViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(clickListener, item)
    }
}

/**
 * Election List Item view holder
 */
class ElectionViewHolder private constructor(val binding: ElectionsListItemBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(clickListener: ElectionListener, item: Election) {
        binding.item = item
        binding.clickListener = clickListener // binding the clickHandler from UI -> layout
        binding.executePendingBindings()
    }

    companion object {
        fun from(parent: ViewGroup): ElectionViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ElectionsListItemBinding.inflate(layoutInflater, parent, false)
            return ElectionViewHolder(binding)
        }
    }
}

/**
 * The DiffCallback is mandatory for using RecyclerListAdapter
 */
class ElectionDiffCallback : DiffUtil.ItemCallback<Election>() {
    override fun areItemsTheSame(oldItem: Election, newItem: Election): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Election, newItem: Election): Boolean {
        return oldItem == newItem
    }

}

/**
 * The interface for propagating click event to UI from list item
 */
class ElectionListener(val clickListener: (item: Election) -> Unit) {
    fun onclick(item: Election) = clickListener(item)
}