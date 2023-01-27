package com.example.android.politicalpreparedness.network.jsonadapter

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import java.text.SimpleDateFormat
import java.util.*

/**
 * The after for parsing json string to Date an vice-versa
 */
class DateAdapter {
    @FromJson
    fun fromDateString(electionDay: String): Date {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        return dateFormat.parse(electionDay) as Date
    }

    @ToJson
    fun toDateString(electionDate: Date): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        return dateFormat.format(electionDate)
    }
}