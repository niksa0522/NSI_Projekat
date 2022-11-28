package com.example.nsiprojekat.sharedViewModels

import android.graphics.Bitmap
import android.text.Editable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AddPlaceViewModel : ViewModel() {
    private val _placeName = MutableLiveData<String>()
    val placeName: LiveData<String> = _placeName

    private val _picture = MutableLiveData<Bitmap>()
    val picture: LiveData<Bitmap> = _picture

    //FIXME: ovo treba da se preuredi!!!
    private val _placeLat = MutableLiveData<String>()
    val placeLat: LiveData<String> = _placeLat
    private val _placeLong = MutableLiveData<String>()
    val placeLong: LiveData<String> = _placeLong


    fun onPlaceNameTextChanged(p0: Editable?) {
        _placeName.value = p0.toString()
        //Log.d("ADDPLACEVM", p0.toString())
    }

    fun onPlaceLatTextChanged(p0: Editable) {
        _placeLat.value = p0.toString()
    }

    fun onPlaceLongTextChanged(p0: Editable) {
        _placeLong.value = p0.toString()
    }

    fun setPicture(picture: Bitmap) {
        _picture.value = picture
    }

    fun setLatLong(lat: Double, long: Double) {
        _placeLat.value = lat.toString()
        _placeLong.value = long.toString()
    }

    fun addPlaceToDB() {
        //TODO: add place to db in places collection, add picture to places in storage
    }
}