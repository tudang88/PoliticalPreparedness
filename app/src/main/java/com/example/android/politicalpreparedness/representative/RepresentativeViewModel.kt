package com.example.android.politicalpreparedness.representative

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.repository.ElectionsRepository
import kotlinx.coroutines.launch
import timber.log.Timber

class RepresentativeViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val repository: ElectionsRepository
) : ViewModel() {

    var selectedStatePos = MutableLiveData<Int>()
    var addressLine1 = MutableLiveData<String>()
    var addressLine2 = MutableLiveData<String>()
    var city = MutableLiveData<String>()
    var zipCode = MutableLiveData<String>()
    val representatives = repository.representatives
    var backupFileName: LiveData<String> = savedStateHandle.getLiveData("backupFileName")

    /**
     * init live data
     */
    init {
        Timber.d("init ViewModel Live Data")
        addressLine1.value = ""
        addressLine2.value = ""
        city.value = ""
        zipCode.value = ""
        selectedStatePos.value = 0
        backupFileName.value?.let {
            viewModelScope.launch {
                repository.restoreFromSavedState(it)
            }
        }

    }

    /**
     * get Address with current
     * input state
     */
    fun getCurrentAddress(): Address {
        val statePosition = selectedStatePos.value ?: 0
        return Address(
            addressLine1.value!!,
            addressLine2.value!!,
            city.value!!,
            allStateList[statePosition],
            zipCode.value!!
        )
    }


    /**
     * set current address to update
     * view
     */
    fun setCurrentAddress(address: Address) {
        Timber.d("set address: $address")
        viewModelScope.launch {
            addressLine1.value = address.line1
            addressLine2.value = address.line2 ?: ""
            city.value = address.city
            zipCode.value = address.zip
            if (allStateList.contains(address.state)) {
                val position = allStateList.indexOf(address.state)
                if (position != selectedStatePos.value) {
                    selectedStatePos.postValue(position)
                }
            }
        }
    }

    /**
     * handling event click on Find Representative Button
     */
    fun onFindRepresentativesClick() {
        viewModelScope.launch {
            repository.getRepresentatives(
                Address(
                    addressLine1.value!!,
                    addressLine2.value!!,
                    city.value!!,
                    allStateList[selectedStatePos.value!!],
                    zipCode.value!!
                )
            )
            // backup data
            val oldBackupFile = backupFileName.value ?: ""
            savedStateHandle["backupFileName"] = repository.backupRepresentatives(oldBackupFile)
        }

    }

    /**
     * the list of states for spinner
     */
    val allStateList = listOf(
        "Alabama",
        "Alaska",
        "Arizona",
        "Arkansas",
        "California",
        "Colorado",
        "Connecticut",
        "Delaware",
        "Florida",
        "Georgia",
        "Hawaii",
        "Idaho",
        "Illinois",
        "Indiana",
        "Iowa",
        "Kansas",
        "Kentucky",
        "Louisiana",
        "Maine",
        "Maryland",
        "Massachusetts",
        "Michigan",
        "Minnesota",
        "Mississippi",
        "Missouri",
        "Montana",
        "Nebraska",
        "Nevada",
        "New Hampshire",
        "New Jersey",
        "New Mexico",
        "New York",
        "North Carolina",
        "North Dakota",
        "Ohio",
        "Oklahoma",
        "Oregon",
        "Pennsylvania",
        "Rhode Island",
        "South Carolina",
        "South Dakota",
        "Tennessee",
        "Texas",
        "Utah",
        "Vermont",
        "Virginia",
        "Washington",
        "Washington DC",
        "West Virginia",
        "Wisconsin",
        "Wyoming"
    )
}
