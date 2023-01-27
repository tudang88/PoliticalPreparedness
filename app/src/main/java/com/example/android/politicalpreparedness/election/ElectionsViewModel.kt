package com.example.android.politicalpreparedness.election

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.repository.ElectionsRepository
import kotlinx.coroutines.launch

class ElectionsViewModel(private val electionsRepository: ElectionsRepository) : ViewModel() {

    /**
     * link livedata from repository to viewModel livedata
     */
    private val _upcomingElections = electionsRepository.upcomingElections
    val upcomingElections: LiveData<List<Election>>
        get() = _upcomingElections

    private val _savedElections = electionsRepository.savedElections
    val savedElection: LiveData<List<Election>>
        get() = _savedElections

    /**
     * load data from repository
     */
    init {
        viewModelScope.launch {
            electionsRepository.getUpcomingElections()
        }
    }
}