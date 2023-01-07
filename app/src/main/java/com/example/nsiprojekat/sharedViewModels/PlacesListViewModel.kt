package com.example.nsiprojekat.sharedViewModels

import android.text.Editable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.nsiprojekat.Firebase.FirestoreModels.Place
import com.example.nsiprojekat.helpers.ActionState
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage


class PlacesListViewModel : ViewModel() {
    private val auth = Firebase.auth
    private val storage = Firebase.storage

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

    private val _deleteState = MutableLiveData<ActionState>(ActionState.Idle)
    val deleteState: LiveData<ActionState> = _deleteState

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

    fun getPlace(id:String){
        db.collection("places").document(id).get().addOnSuccessListener {
            selectedPlace=it.toObject<Place>()
        }
    }

    fun onDistanceFilterCheckedChanged() {
        _distanceFilterOn.value = !_distanceFilterOn.value!!
    }

    fun setNameQuery() {
        if (placeNameFilter.value != null && _nameFilterOn.value!!) {
            query = db.collection("places").orderBy("name")
                .whereArrayContains("nameQueryList", placeNameFilter.value!!)
        }
    }

    private fun resetNameFilter() {
        query = db.collection("places").orderBy("name")
    }

    fun checkPlaceCreator(): Boolean {
        return auth.currentUser!!.uid == selectedPlace!!.creatorId
    }

    fun deletePlace() {
        storage.reference.child("places").child(selectedPlace!!.id!!)
            .child(selectedPlace!!.id!! + ".jpg").delete()
            .addOnSuccessListener {
                db.collection("places").document(selectedPlace!!.id!!).delete()
                    .addOnSuccessListener { _deleteState.value = ActionState.Success }
                    .addOnFailureListener { _deleteState.value = ActionState.ActionError(it.message + "db") }
            }
            .addOnFailureListener{ _deleteState.value = ActionState.ActionError(it.message + "storage") }
    }

    fun resetDeleteState() {
        _deleteState.value = ActionState.Idle
    }

    var updateSelectedPlace = { place: Place ->
        selectedPlace = place
    }

}