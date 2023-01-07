package com.example.nsiprojekat.screens.placesFilters

import android.Manifest
import android.annotation.SuppressLint
import android.location.Location
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.nsiprojekat.R
import com.example.nsiprojekat.databinding.FragmentAddPlaceBinding
import com.example.nsiprojekat.databinding.FragmentPlaceFiltersBinding
import com.example.nsiprojekat.helpers.PermissionHelper
import com.example.nsiprojekat.sharedViewModels.AddPlaceViewModel
import com.example.nsiprojekat.sharedViewModels.PlacesListViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class PlaceFiltersFragment : Fragment() {

    private val viewModel: PlacesListViewModel by activityViewModels()
    private var _binding: FragmentPlaceFiltersBinding? = null
    private val binding get() = _binding!!

    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var currentLocation = LatLng(
        0.0, 0.0
    )
    private var lastLocation: Location? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_place_filters, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.data = viewModel
        binding.cbNameFilter.isChecked = viewModel.nameFilterOn.value!!

        val placeNameFilterET = binding.etPlaceNameFilter
        val placeDistanceFilterET = binding.etPlaceDistanceFilter
        initData(placeNameFilterET, placeDistanceFilterET)

        binding.btnSetFilters.setOnClickListener { setFilters() }
//        binding.cbNameFilter.setOnCheckedChangeListener { buttonView, isChecked ->
//            if (!isChecked) {
//                viewModel.resetNameFilter()
//            }
//            viewModel.nameFilterToggled = isChecked
//        }

        if(PermissionHelper.isLocationPermissionGranted(requireContext())) {
            if(fusedLocationClient!=null){
                fusedLocationClient?.lastLocation?.addOnCompleteListener {
                    if(it.result!=null) {
                        currentLocation = LatLng(it.result.latitude, it.result.longitude)
                        viewModel.currentLocation = currentLocation
                    }
                }
            } else {
                setupLocationTrackingWithLocation()
            }
        }
        else {
            requestPermissionLauncherFLC.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }

    }

    private fun initData(placeNameFilterET: EditText, placeDistanceFilterET: EditText) {
        placeNameFilterET.setText(viewModel.placeNameFilter.value ?: "")
        placeDistanceFilterET.setText(viewModel.placeDistanceFilter.value ?: "")
    }

    private fun setFilters() {
        viewModel.setNameQuery()
        findNavController().navigate(R.id.action_placeFiltersFragment_to_nav_places)
    }



    private val requestPermissionLauncherFLC = registerForActivityResult(
        ActivityResultContracts.RequestPermission()) {
            isGranted: Boolean->
        if(isGranted) {
            setupLocationTracking()
        }
    }

    private fun setupLocationTracking() {
        if(PermissionHelper.isLocationPermissionGranted(requireContext())) {
            setupFusedLocationClient()
            setupLocationUpdates()
        } else {
            requestPermissionLauncherFLC.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    @SuppressLint("MissingPermission")
    private fun setupLocationTrackingWithLocation() {
        if (PermissionHelper.isLocationPermissionGranted(requireContext())) {
            setupFusedLocationClient()
            setupLocationUpdates()
        } else {
            requestPermissionLauncherFLC.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun setupLocationUpdates(){
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        fusedLocationClient?.lastLocation?.addOnSuccessListener {
            if (it != null) {
                lastLocation = it
                currentLocation = LatLng(it.latitude, it.longitude)
                viewModel.currentLocation = currentLocation
            }
        }
    }


    private fun setupFusedLocationClient(){
        val mLocationRequest = com.google.android.gms.location.LocationRequest.create()
        mLocationRequest.interval = 60000
        mLocationRequest.fastestInterval = 5000
        mLocationRequest.priority = com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
        val mLocationCallback: LocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                if (locationResult == null) {
                    return
                }
                for (location in locationResult.locations) {
                    if (location != null) {
                    }
                }
            }
        }
        LocationServices.getFusedLocationProviderClient(context!!)
            .requestLocationUpdates(mLocationRequest, mLocationCallback, null)
    }

    override fun onStart() {
        super.onStart()
    }


}