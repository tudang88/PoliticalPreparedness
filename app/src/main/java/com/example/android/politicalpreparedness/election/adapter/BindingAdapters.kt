package com.example.android.politicalpreparedness.election.adapter

import android.widget.Button
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.network.models.Election

/**
 * binding elections list to recycler view
 */
@BindingAdapter("listElections")
fun bindingElectionsRecyclerView(recyclerView: RecyclerView, data: List<Election>?) {
    data?.let {
        (recyclerView.adapter as ElectionListAdapter).submitList(data)
    }
}

@BindingAdapter("savedElectionState")
fun bindingSavedElection(button: Button, savedState: Boolean) {
    if (savedState) {
        button.text = button.context.getString(R.string.unfollow_election)
    } else {
        button.text = button.context.getString(R.string.follow_election)
    }
}