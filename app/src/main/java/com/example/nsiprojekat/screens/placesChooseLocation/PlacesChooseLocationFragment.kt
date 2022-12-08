package com.example.nsiprojekat.screens.placesChooseLocation

import android.Manifest
import android.annotation.SuppressLint
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.nsiprojekat.R
import com.example.nsiprojekat.databinding.FragmentPlacesChooseLocationBinding
import com.example.nsiprojekat.helpers.PermissionHelper
import com.example.nsiprojekat.sharedViewModels.AddPlaceViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

class PlacesChooseLocationFragment : Fragment() {

    //FIXME: this is shitcode and needs revisions asap

    private val viewModel: AddPlaceViewModel by activityViewModels()
    private var fusedLocationClient: FusedLocationProviderClient? = null
    private lateinit var map: GoogleMap
    private var currentLocation = LatLng(
        0.0, 0.0
    )
    private var lastLocation: Location? = null
    private var markerPos: LatLng? = null

    private var _binding: FragmentPlacesChooseLocationBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlacesChooseLocationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val supportMapFragment: SupportMapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        supportMapFragment.getMapAsync { googleMap ->
            map = googleMap
            if(PermissionHelper.isLocationPermissionGranted(requireContext())) {
                enableMyLocation()
                if(fusedLocationClient!=null){
                    fusedLocationClient?.lastLocation?.addOnCompleteListener {
                        if(it.result!=null) {
                            currentLocation = LatLng(it.result.latitude, it.result.longitude)
                            val location = LatLng(currentLocation.latitude, currentLocation.longitude)

                            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 16.0f))
                        }
                    }
                } else {
                    setupLocationTrackingWithLocation()
                }
            }
            else {
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
            googleMap.uiSettings.isZoomControlsEnabled = true
            googleMap.setOnMarkerDragListener(object : OnMarkerDragListener {
                override fun onMarkerDragStart( marker: Marker) {}
                override fun onMarkerDrag( marker: Marker) {}
                override fun onMarkerDragEnd( marker: Marker) {
                    markerPos = marker.position
                }
            })
        }

        binding.fabAdd.setOnClickListener {
            viewModel.setLatLong(
                markerPos!!.latitude,
                markerPos!!.longitude
            )
            findNavController().navigate(R.id.action_placesChooseLocationFragment_to_addPlaceFragment)
        }
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
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        } else {
            requestPermissionLauncherFLC.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    @SuppressLint("MissingPermission")
    private fun enableMyLocation() {
        if (PermissionHelper.isLocationPermissionGranted(requireContext())) {
            map.isMyLocationEnabled = true
        }
        else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()){
            isGranted: Boolean->
        if(isGranted){
            enableMyLocation()
        }
    }

    @SuppressLint("MissingPermission")
    private fun setupLocationTrackingWithLocation() {
        if(PermissionHelper.isLocationPermissionGranted(requireContext())){
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
            fusedLocationClient?.lastLocation?.addOnCompleteListener {
                if(it.result!=null) {
                    map.moveCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            LatLng(
                                it.result.latitude,
                                it.result.longitude
                            ), 16f
                        )
                    )
                    lastLocation=it.result
                    map.addMarker(
                        MarkerOptions().position(LatLng(it.result.latitude, it.result.longitude)).draggable(true).title("Current Location")
                    )
                }
            }
        } else {
            requestPermissionLauncherFLC.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }
}