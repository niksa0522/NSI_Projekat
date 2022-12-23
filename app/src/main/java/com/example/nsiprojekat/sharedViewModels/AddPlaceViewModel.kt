package com.example.nsiprojekat.sharedViewModels

import android.graphics.Bitmap
import android.text.Editable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.nsiprojekat.Firebase.FirestoreModels.Place
import com.example.nsiprojekat.helpers.ActionState
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream
import java.util.UUID

class AddPlaceViewModel : ViewModel() {

    private val db = Firebase.firestore
    private val auth = Firebase.auth

    private val _actionState = MutableLiveData<ActionState>()
    val actionState: LiveData<ActionState> = _actionState

    private val _placeName = MutableLiveData<String>()
    val placeName: LiveData<String> = _placeName

    private val _picture = MutableLiveData<Bitmap>()
    val picture: LiveData<Bitmap> = _picture

    private val _placeLat = MutableLiveData<String>()
    val placeLat: LiveData<String> = _placeLat
    private val _placeLong = MutableLiveData<String>()
    val placeLong: LiveData<String> = _placeLong


    fun onPlaceNameTextChanged(p0: Editable?) {
        _placeName.value = p0.toString()
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
        if (checkData()) {
            val uuid = UUID.randomUUID().toString()
            val storage = Firebase.storage
            val imageRef: StorageReference = storage.reference.child("places").child(uuid).child("${uuid}.jpg")
            val baos = ByteArrayOutputStream()
            val bitmap = picture.value
            bitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val data = baos.toByteArray()
            val uploadTask = imageRef.putBytes(data)
            val urlTask = uploadTask.continueWithTask{ task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        _actionState.value = ActionState.ActionError("Upload error: ${it.message}")
                    }
                }
                imageRef.downloadUrl
            }.addOnCompleteListener{task ->
                if(task.isSuccessful){
                    val imageUrl = task.result.toString()
                    val place = Place(uuid, auth.currentUser!!.uid, _placeName.value, _placeLat.value, _placeLong.value, imageUrl, makeSubstrings(_placeName.value!!))
                    db.collection("places").document(uuid)
                        .set(place)
                        .addOnSuccessListener {
                            _actionState.value = ActionState.Success
                        }
                        .addOnFailureListener { e ->
                            imageRef.delete()
                            _actionState.value = ActionState.ActionError("Upload error: ${e.message}")
                        }
                }
            }
        }
    }

    fun resetAddPlace() {
        _actionState.value = ActionState.Idle
        _placeLat.value = ""
        _placeLong.value = ""
        _placeName.value = ""
        _picture.value = null
    }

    private fun checkData(): Boolean {
        if (_placeName.value!!.isBlank()) {
            _actionState.value = ActionState.ActionError("Uneti ime mesta!")
            return false
        }
        if (_placeLat.value!!.isBlank()) {
            _actionState.value = ActionState.ActionError("Potreban je latitude!")
            return false
        }
        if (_placeLong.value!!.isBlank()) {
            _actionState.value = ActionState.ActionError("Potreban je longitude!")
            return false
        }
        if (_picture.value == null) {
            _actionState.value = ActionState.ActionError("Potrebna je slika!")
            return false
        }
        return true
    }

    private fun makeSubstrings(string: String): List<String> {
        val size = string.length
        val substrings: MutableList<String> = mutableListOf()
        for (substringSize in 1..size) {
            substrings.add(string.substring(0, substringSize))
        }
        return substrings
    }
}