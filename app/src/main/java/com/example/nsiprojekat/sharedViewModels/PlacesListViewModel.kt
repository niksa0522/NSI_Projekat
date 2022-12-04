package com.example.nsiprojekat.sharedViewModels

import android.text.Editable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class PlacesListViewModel : ViewModel() {
    private val db = Firebase.firestore
    val query = db.collection("places").orderBy("name")

    private val _placeNameFilter = MutableLiveData<String>()
    val placeNameFilter: LiveData<String> = _placeNameFilter

    private val _placeDistanceFilter = MutableLiveData<String>()
    val placeDistanceFilter: LiveData<String> = _placeDistanceFilter

    private val _nameFilterOn = MutableLiveData<Boolean>(false)
    val nameFilterOn: LiveData<Boolean> = _nameFilterOn

    private val _distanceFilterOn = MutableLiveData<Boolean>(false)
    val distanceFilterOn: LiveData<Boolean> = _distanceFilterOn

    // TODO: queries for place name and location

    fun onPlaceNameFilterChanged(p0: Editable?) {
        _placeNameFilter.value = p0.toString()
    }

    fun onPlaceDistanceFilterChanged(p0: Editable?) {
        _placeDistanceFilter.value = p0.toString()
    }

    fun onNameFilterCheckedChanged() {
        _nameFilterOn.value = !_nameFilterOn.value!!
    }

    fun onDistanceFilterCheckedChanged() {
        _distanceFilterOn.value = !_distanceFilterOn.value!!
    }

}