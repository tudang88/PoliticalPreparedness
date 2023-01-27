package com.example.android.politicalpreparedness.network.jsonadapter

import com.example.android.politicalpreparedness.network.models.Division
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.ElectionResponse
import com.squareup.moshi.*
import timber.log.Timber
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.*

/**
 * The manual adapter for parsing ElectionResponse
 * only use for practice
 */
class MyElectionsAdapter : JsonAdapter<ElectionResponse>() {
    /**
     * Every JsonAdapter requires FACTORY to work
     */
    companion object {
        val FACTORY: Factory = object : Factory {
            override fun create(
                type: Type,
                annotations: Set<Annotation>,
                moshi: Moshi
            ): JsonAdapter<*>? {
                if (type === ElectionResponse::class.java) {
                    return MyElectionsAdapter()
                }
                return null
            }
        }
    }

    @FromJson
    override fun fromJson(reader: JsonReader): ElectionResponse? {
        Timber.tag("MyElectionAdapter").d("fromElectionsResponse to object")
        val electionsList = mutableListOf<Election>()
        var kind: String = "none"
        with(reader) {
            beginObject()
            while (hasNext()) {
                when (nextName()) {
                    "elections" -> {
                        electionsList.addAll(electionFromJson(this))
                        Timber.d("Found list $electionsList")
                    }
                    "kind" -> {
                        kind = nextString()
                        Timber.d("Found kind: $kind")
                    }
                }
            }
            endObject()
        }
        return ElectionResponse(kind, electionsList)
    }

    @ToJson
    override fun toJson(writer: JsonWriter, value: ElectionResponse?) {
    }

    private fun electionFromJson(reader: JsonReader): List<Election> {
        val resultList = mutableListOf<Election>()
        var id = 0L
        var name = ""
        var electionDay = ""
        var ocdDivisionId = ""
        with(reader) {
            beginArray()
            while (hasNext()) {

                beginObject()// start of one election
                while (hasNext()) {
                    when (nextName()) {
                        "id" -> {
                            id = nextLong()
                        }
                        "name" -> {
                            name = nextString()
                        }
                        "electionDay" -> {
                            electionDay = nextString()
                        }
                        "ocdDivisionId" -> {
                            ocdDivisionId = nextString()
                        }
                        else -> skipValue()
                    }
                }
                endObject()// end of one election
                // create election
                resultList.add(
                    Election(
                        id,
                        name,
                        convertStringToDate(electionDay),
                        divisionFromJson(ocdDivisionId)
                    )
                )
                // continue to next array item
            }
            endArray()
        }
        return resultList
    }

    private fun convertStringToDate(dateString: String): Date {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        return dateFormat.parse(dateString) as Date
    }

    private fun divisionFromJson(ocdDivisionId: String): Division {
        val countryDelimiter = "country:"
        val stateDelimiter = "state:"
        val country = ocdDivisionId.substringAfter(countryDelimiter, "")
            .substringBefore("/")
        val state = ocdDivisionId.substringAfter(stateDelimiter, "")
            .substringBefore("/")
        return Division(ocdDivisionId, country, state)
    }
}