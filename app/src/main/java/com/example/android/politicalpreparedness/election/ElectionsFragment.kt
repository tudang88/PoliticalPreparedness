package com.example.android.politicalpreparedness.election

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.FragmentElectionBinding
import com.example.android.politicalpreparedness.election.adapter.ElectionListAdapter
import com.example.android.politicalpreparedness.election.adapter.ElectionListener
import com.example.android.politicalpreparedness.network.models.Election
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class ElectionsFragment : Fragment() {

    private lateinit var binding: FragmentElectionBinding

    private val _viewModel: ElectionsViewModel by viewModel()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_election, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = _viewModel
        binding.upcomingElectionsList.adapter = ElectionListAdapter(ElectionListener {
            Timber.d("UpcomingElections click item : $it")
            Timber.d("electionId: ${it.id}")
            Timber.d("division: ${it.division}")
            navigateToVoterInfo(it)
        })
        binding.savedElectionsList.adapter = ElectionListAdapter(ElectionListener {
            Timber.d("SavedElections click item : $it")
            navigateToVoterInfo(it)
        })

        return binding.root

    }

    /**
     * helper to navigate to VoterInfo Fragment
     */
    private fun navigateToVoterInfo(election: Election) {
        findNavController().navigate(
            ElectionsFragmentDirections.actionElectionsFragmentToVoterInfoFragment(
                election.id,
                election.division
            )
        )
    }
}