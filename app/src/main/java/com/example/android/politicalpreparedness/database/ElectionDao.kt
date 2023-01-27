package com.example.android.politicalpreparedness.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.android.politicalpreparedness.network.models.Election

@Dao
interface ElectionDao {
    /**
     * Insert one election to database
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertElection(election: Election)

    /**
     * get all current saved elections
     */
    @Query("SELECT * from election_table")
    fun getAllSavedElections(): LiveData<List<Election>>

    /**
     * get specific election by id
     */
    @Query("SELECT * FROM election_table WHERE id = :id")
    fun getSavedElectionById(id: Long): Election?


    /**
     * delete one saved Elections from DB
     */
    @Query("DELETE FROM election_table WHERE id = :id")
    fun deleteSavedElectionById(id: Long)


    /**
     * clear database
     */
    @Query("DELETE FROM election_table")
    fun clearSavedElection()
}