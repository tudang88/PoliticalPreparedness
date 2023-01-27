package com.example.android.politicalpreparedness.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.*
import com.example.android.politicalpreparedness.representative.model.Representative
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

/**
 * The repository for Election
 * The ElectionFragmentViewModel will retrieve data via this repository
 * not directly interactive to retrofit
 */
class ElectionsRepository(
    private val saveDb: ElectionDatabase,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    /**
     * Live data for managing upcoming elections
     */
    private val _upcomingElections = MutableLiveData<List<Election>>()
    val upcomingElections: LiveData<List<Election>>
        get() = _upcomingElections

    /**
     * Live data for managing saved elections
     */
    private val _savedElections = saveDb.electionDao.getAllSavedElections()
    val savedElections: LiveData<List<Election>>
        get() = _savedElections

    private val _representatives = MutableLiveData<List<Representative>>()
    val representatives: LiveData<List<Representative>>
        get() = _representatives

    /**
     * VoterInfo Response
     */
    private val _voterInfo = MutableLiveData<VoterInfoResponse>()
    val voterInfo: LiveData<VoterInfoResponse>
        get() = _voterInfo

    /**
     * get upcoming election from Google Civic Api
     */
    suspend fun getUpcomingElections() {
        Timber.d("getUpcomingElections Elections")
        var upcomingResult = listOf<Election>()
        withContext(ioDispatcher) {
            try {
                val response = CivicsApi.retrofitService.getElections()
                upcomingResult = response.elections
            } catch (e: Exception) {
                Timber.e("Failure: ${e.localizedMessage}")
            }
        }
        // update UI
        withContext(Dispatchers.Main) {
            _upcomingElections.value = upcomingResult
        }
    }

    /**
     * add SavedElection to local database
     */
    suspend fun addSavedElection(election: Election) {
        Timber.d("insert Election to DB")
        withContext(ioDispatcher) {
            saveDb.electionDao.insertElection(election)
        }
    }

    /**
     * get election from database by specific Id
     */
    suspend fun getElectionById(electionId: Long): Election? {
        Timber.d("get Election from DB")
        var result: Election? = null
        withContext(ioDispatcher) {
            result = saveDb.electionDao.getSavedElectionById(electionId)
        }
        return result
    }

    /**
     * remove SavedElection to local database
     */
    suspend fun removeSavedElection(id: Long) {
        Timber.d("remove Election to DB")
        withContext(ioDispatcher) {
            saveDb.electionDao.deleteSavedElectionById(id)
        }
    }

    /**
     * clear all save Elections DB
     */
    suspend fun clearAllSavedElection() {
        Timber.d("remove Election to DB")
        withContext(ioDispatcher) {
            saveDb.electionDao.clearSavedElection()
        }
    }

    /**
     * get voter info
     */
    suspend fun getVoterInfo(address: String, electionId: Long): VoterInfoResponse? {
        Timber.d("getVoterInfo() from Google Civic")
        var result: VoterInfoResponse? = null
        withContext(ioDispatcher) {
            try {
                val response = CivicsApi.retrofitService.getVoterInfo(address, electionId)
                result = response
                Timber.d("VotingLocationUrl: ${response.state?.get(0)?.electionAdministrationBody?.votingLocationFinderUrl}")
                Timber.d("BallotInfoUrl: ${response.state?.get(0)?.electionAdministrationBody?.ballotInfoUrl}")
            } catch (e: Exception) {
                Timber.e("Failure: ${e.localizedMessage}")
                result = null
            }
        }
        return result
    }

    /**
     * get Representatives
     */
    suspend fun getRepresentatives(address: Address) {
        val result = mutableListOf<Representative>()
        Timber.d("getRepresentatives from Google civic")
        withContext(ioDispatcher) {
            try {
                val response =
                    CivicsApi.retrofitService.getRepresentatives(address.toFormattedString())
                result.addAll(response.offices.flatMap { it.getRepresentatives(response.officials) })
            } catch (e: Exception) {
                Timber.e("Failure: ${e.localizedMessage}")
            }
        }
        // update UI
        withContext(Dispatchers.Main) {
            _representatives.postValue(result)
        }
    }
}