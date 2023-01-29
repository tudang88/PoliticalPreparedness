@file:Suppress("BlockingMethodInNonBlockingContext")

package com.example.android.politicalpreparedness.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.jsonadapter.DateAdapter
import com.example.android.politicalpreparedness.network.jsonadapter.ElectionAdapter
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.VoterInfoResponse
import com.example.android.politicalpreparedness.representative.model.Representative
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File

/**
 * The repository for Election
 * The ElectionFragmentViewModel will retrieve data via this repository
 * not directly interactive to retrofit
 */
class ElectionsRepository(
    private val saveDb: ElectionDatabase,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    companion object {
        private const val FILE_NAME_PREFIX = "backup_representatives"
        private const val FILE_NAME_SUFFIX = ""
    }

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
     * backup list of Representative
     */

    suspend fun backupRepresentatives(currentFile: String): String {
        // delete previous file because it's no longer used
        deleteBackupFile(currentFile)
        val moshi = Moshi.Builder().add(ElectionAdapter()).add(KotlinJsonAdapterFactory())
            .add(DateAdapter()).build()
        val type = Types.newParameterizedType(List::class.java, Representative::class.java)
        val jsonAdapter: JsonAdapter<List<Representative>> = moshi.adapter(type)
        val tempFile = File.createTempFile(FILE_NAME_PREFIX, FILE_NAME_SUFFIX)
        withContext(ioDispatcher) {
            val jsonObject = jsonAdapter.toJson(_representatives.value)
            try {
                val writeBuffer = BufferedWriter(tempFile.writer())
                writeBuffer.write(jsonObject.toString())
                writeBuffer.close()
            } catch (e: Exception) {
                Timber.e("write temp file error: ${e.localizedMessage}")
            }
        }
        return tempFile.absolutePath
    }

    /**
     * delete old file
     */
    suspend fun deleteBackupFile(fileName: String) {
        withContext(ioDispatcher) {
            val file = File(fileName)
            if (!file.exists()) {
                Timber.d("Try to delete but file not exist $fileName")
                return@withContext
            }
            file.delete()
        }
    }

    /**
     * restore from saved state
     */
    suspend fun restoreFromSavedState(fileName: String) {
        val file = File(fileName)
        if (!file.exists()) {
            Timber.d("file not exist $fileName")
            return
        }
        val moshi = Moshi.Builder().add(ElectionAdapter()).add(KotlinJsonAdapterFactory())
            .add(DateAdapter()).build()
        val type = Types.newParameterizedType(List::class.java, Representative::class.java)
        val jsonAdapter: JsonAdapter<List<Representative>> = moshi.adapter(type)
        var savedPresentatives: List<Representative> = listOf()
        withContext(ioDispatcher) {
            var json = ""
            try {
                val reader = BufferedReader(file.reader())
                val stringBuilder = StringBuilder()
                var line = reader.readLine()
                while (line != null) {
                    stringBuilder.append(line).append("\n")
                    line = reader.readLine()
                }
                reader.close()
                json = stringBuilder.toString()
            } catch (e: Exception) {
                Timber.e("read json file error: ${e.localizedMessage}")
            }
            savedPresentatives = jsonAdapter.fromJson(json) as List<Representative>
        }
        // update UI
        withContext(Dispatchers.Main) {
            _representatives.postValue(savedPresentatives)
        }

    }

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