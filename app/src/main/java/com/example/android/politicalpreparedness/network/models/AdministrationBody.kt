package com.example.android.politicalpreparedness.network.models

import com.squareup.moshi.JsonClass

/**
 * the election InfoUrl
 * and
 * the ballotInfoUrl
 * will be used to open web browser when user click on related links on
 * VoterInfo page
 */
@JsonClass(generateAdapter = true)
data class AdministrationBody (
        val name: String? = null,
        val electionInfoUrl: String? = null,
        val votingLocationFinderUrl: String? = null,
        val ballotInfoUrl: String? = null,
        val correspondenceAddress: Address? = null
)