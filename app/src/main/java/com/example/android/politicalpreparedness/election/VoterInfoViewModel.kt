package com.example.android.politicalpreparedness.election

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.network.models.Division
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.repository.ElectionsRepository
import kotlinx.coroutines.launch
import timber.log.Timber

class VoterInfoViewModel(private val dataSource: ElectionsRepository) : ViewModel() {

    /**
     * livedata will be observed by View
     */
    private val _followedState = MutableLiveData<Boolean>()
    val followedState: LiveData<Boolean>
        get() = _followedState
    private val _electionName = MutableLiveData<String>()
    val electionName: LiveData<String>
        get() = _electionName

    private val _electionDate = MutableLiveData<String>()
    val electionDate: LiveData<String>
        get() = _electionDate

    private val _stateHeader = MutableLiveData<String>()
    val stateHeader: LiveData<String>
        get() = _stateHeader

    private val _address = MutableLiveData<String>()
    val address: LiveData<String>
        get() = _address

    private val _votingLocationUrl = MutableLiveData<String>()
    val votingLocationUrl: LiveData<String>
        get() = _votingLocationUrl

    private val _ballotInfoUrl = MutableLiveData<String>()
    val ballotInfoUrl: LiveData<String>
        get() = _ballotInfoUrl

    private val _openLink = MutableLiveData<String>()
    val openLink: LiveData<String>
        get() = _openLink
    private var currentElection: Election? = null

    private val _error = MutableLiveData<Boolean>()
    val error: LiveData<Boolean>
        get() = _error

    init {
        _error.value = false
        _stateHeader.value = ""
        _address.value = ""
        _votingLocationUrl.value = ""
        _ballotInfoUrl.value = ""
        _openLink.value = null
    }

    /**
     * the function will update the following info
     * - followedState -> true if election already in local database, otherwise false
     * - get info from google civics for updating the election link
     */
    fun start(electionId: Long, division: Division) {
        // verify arguments
        if (electionId <= 0L || division.state == "") {
            _error.value = true
        }
        // confirm database
        viewModelScope.launch {
            val savedElection = dataSource.getElectionById(electionId)
            if (null != savedElection) {
                Timber.d("Found already followed election")
                _electionName.value = savedElection.name
                _followedState.value = true
                _electionDate.value = savedElection.electionDay.toString()
                currentElection = savedElection
            }
            // get voter info from google civics
            val response = dataSource.getVoterInfo(division.state, electionId)
            if (null != response) {
                Timber.d("get voterInfo response successful")
                _electionName.value = response.election.name
                _electionDate.value = response.election.electionDay.toString()
                currentElection = response.election
                _stateHeader.value = response.state?.get(0)?.name
                _address.value = response.pollingLocations
                _votingLocationUrl.value =
                    response.state?.get(0)?.electionAdministrationBody?.votingLocationFinderUrl
                _ballotInfoUrl.value =
                    response.state?.get(0)?.electionAdministrationBody?.ballotInfoUrl
                Timber.d("stateHeader: ${_stateHeader.value}")
            }
            // show error when nothing retrieved
            else {
                Timber.d("Not followed yet election")
                _electionName.value = "Not Found Election"
                _followedState.value = false
                _electionDate.value = "Not Found Election Date"
            }
        }

    }

    /**
     * handling when user click on voting location
     */
    fun onClickVotingLocation() {
        Timber.d("click voting location")
        _openLink.value = _votingLocationUrl.value
    }

    /**
     * handling when user click on ballot info link
     */
    fun onClickBallotInformation() {
        Timber.d("click ballot information")
        _openLink.value = _ballotInfoUrl.value
    }

    /**
     * reset the live data after handling open webview request done
     */
    fun openLinkDone() {
        _openLink.value = null
    }

    /**
     * handling when user click follow/unfollow
     * follow -> write to local database
     * unfollow -> delete from database
     */
    fun onFollowUnFollow() {
        viewModelScope.launch {
            if (currentElection == null) {
                Timber.d("current election is null")
                _error.value = true
                return@launch
            }
            currentElection?.let {
                if (_followedState.value == true) {
                    // change to unfollow
                    Timber.d("Unfollow election: $currentElection")
                    dataSource.removeSavedElection(currentElection!!.id)
                    _followedState.value = false

                } else {
                    // change to follow
                    Timber.d("Follow election: $currentElection")
                    dataSource.addSavedElection(it)
                    _followedState.value = true
                }
                _error.value = false
            }
        }
    }


    /**
     * livedata for tracking error state
     */
    fun errorHandlingFinished() {
        _error.value = false
    }
}