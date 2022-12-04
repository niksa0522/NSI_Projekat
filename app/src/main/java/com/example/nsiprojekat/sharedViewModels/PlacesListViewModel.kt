package com.example.nsiprojekat.sharedViewModels

import android.text.Editable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.nsiprojekat.Firebase.FirestoreModels.Place
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class PlacesListViewModel : ViewModel() {
    private val db = Firebase.firestore
    var query = db.collection("places").orderBy("name")

    private val _placeNameFilter = MutableLiveData<String>()
    val placeNameFilter: LiveData<String> = _placeNameFilter

    private val _placeDistanceFilter = MutableLiveData<String>()
    val placeDistanceFilter: LiveData<String> = _placeDistanceFilter

    private val _nameFilterOn = MutableLiveData(false)
    val nameFilterOn: LiveData<Boolean> = _nameFilterOn

    private val _distanceFilterOn = MutableLiveData(false)
    val distanceFilterOn: LiveData<Boolean> = _distanceFilterOn

    var currentLocation: LatLng? = null
    var selectedPlace: Place? = null

    fun onPlaceNameFilterChanged(p0: Editable?) {
        _placeNameFilter.value = p0.toString()
    }

    fun onPlaceDistanceFilterChanged(p0: Editable?) {
        _placeDistanceFilter.value = p0.toString()
    }

    fun onNameFilterCheckedChanged() {
        _nameFilterOn.value = !_nameFilterOn.value!!
        if (!_nameFilterOn.value!!) {
            resetNameFilter()
        }
    }

    fun onDistanceFilterCheckedChanged() {
        _distanceFilterOn.value = !_distanceFilterOn.value!!
    }

    fun setNameQuery() {
        if (placeNameFilter.value != null) {
            query = db.collection("places").orderBy("name").whereArrayContains("nameQueryList", placeNameFilter.value!!)
        }
    }

    private fun resetNameFilter() {
        query = db.collection("places").orderBy("name")
    }

}