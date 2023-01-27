package com.example.android.politicalpreparedness.representative

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.FragmentRepresentativeBinding
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.representative.adapter.RepresentativeListAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import java.util.Locale

class RepresentativeFragment : Fragment() {
    private lateinit var binding: FragmentRepresentativeBinding

    companion object {
        private const val REQUEST_LOCAL_PERMISSION = 1
    }

    private val _viewModel: RepresentativeViewModel by viewModel()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_representative, container, false)
        binding.viewModel = _viewModel
        binding.lifecycleOwner = this

        binding.representativeList.adapter = RepresentativeListAdapter()

        binding.buttonLocation.setOnClickListener {
            if (checkLocationPermissions()) {
                getLocation()?.let { location ->
                    _viewModel.onMyLocationClick(geoCodeLocation(location))
                }
                Timber.d("My Location: ${getLocation()}")
            }
            // hide keyboard if it's showing
            hideKeyboard()
        }

        return binding.root
    }

    /**
     * utility for checking permission has been granted or not
     */
    private fun isPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireActivity(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * callback after finishing location enabling
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_LOCAL_PERMISSION) {
            if (grantResults.isNotEmpty() && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                enableMyLocation()
            }
        }
    }

    /**
     * request user enable location
     */
    private fun enableMyLocation() {
        if (isPermissionGranted()) {
            if (ContextCompat.checkSelfPermission(
                    requireActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                    requireActivity(),
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
        } else {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCAL_PERMISSION
            )
        }
    }

    /**
     * confirm location permission
     * before get location for searching
     */
    private fun checkLocationPermissions(): Boolean {
        return if (isPermissionGranted()) {
            Timber.d("Permission is granted")
            true
        } else {
            Timber.d("Permission is not granted -> request enable Location Permission")
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCAL_PERMISSION
            )
            false
        }
    }

    /**
     * get current location
     */
    @SuppressLint("MissingPermission")
    private fun getLocation(): Location? {
        val locationManager =
            requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val providers = locationManager.getProviders(true)
        var location: Location? = null

        for (provider in providers) {
            val loc = locationManager.getLastKnownLocation(provider) ?: continue
            if (location == null || loc.accuracy < location.accuracy) {
                location = loc
            }
        }
        return location
    }

    /**
     * Extract address from location
     */
    private fun geoCodeLocation(location: Location): Address {
        val geocoder = Geocoder(context, Locale.getDefault())
        return geocoder.getFromLocation(location.latitude, location.longitude, 1)
            .map { address ->
                Address(
                    address.thoroughfare,
                    address.subThoroughfare,
                    address.locality,
                    address.adminArea,
                    address.postalCode
                )
            }
            .first()
    }

    /**
     * hide softKeyboard when it's not necessary
     */
    private fun hideKeyboard() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view!!.windowToken, 0)
    }

}