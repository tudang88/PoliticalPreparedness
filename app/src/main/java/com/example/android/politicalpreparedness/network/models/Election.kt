package com.example.android.politicalpreparedness.network.models

import androidx.room.*
import com.squareup.moshi.*
import java.util.*

@JsonClass(generateAdapter = true)
@Entity(tableName = "election_table")
data class Election(
        @PrimaryKey val id: Long,
        @ColumnInfo(name = "name")val name: String,
        @ColumnInfo(name = "electionDay")val electionDay: Date,
        @Embedded(prefix = "division_") @Json(name="ocdDivisionId") val division: Division
)