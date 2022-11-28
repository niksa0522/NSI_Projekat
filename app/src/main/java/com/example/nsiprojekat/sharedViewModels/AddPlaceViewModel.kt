package com.example.nsiprojekat.sharedViewModels

import android.graphics.Bitmap
import android.net.Uri
import android.text.Editable
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.nsiprojekat.Firebase.FirestoreModels.Place
import com.example.nsiprojekat.Firebase.RealtimeModels.User
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream
import java.util.UUID

class AddPlaceViewModel : ViewModel() {

    val db = Firebase.firestore

    private val _uploadState = MutableLiveData<UploadState>()
    val uploadState: LiveData<UploadState> = _uploadState

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
                        _uploadState.value = UploadState.UploadError("Upload error: ${it.message}")
                    }
                }
                imageRef.downloadUrl
            }.addOnCompleteListener{task ->
                if(task.isSuccessful){
                    val imageUrl = task.result.toString()
                    val place = Place(uuid, _placeName.value, _placeLat.value, _placeLong.value, imageUrl)
                    db.collection("places").document(uuid)
                        .set(place)
                        .addOnSuccessListener {
                            _uploadState.value = UploadState.Success
                        }
                        .addOnFailureListener { e ->
                            imageRef.delete()
                            _uploadState.value = UploadState.UploadError("Upload error: ${e.message}")
                        }
                }
            }
        }
    }

    private fun checkData(): Boolean {
        if (_placeName.value!!.isBlank()) {
            _uploadState.value = UploadState.UploadError("Uneti ime mesta!")
            return false
        }
        if (_placeLat.value!!.isBlank()) {
            _uploadState.value = UploadState.UploadError("Potreban je latitude!")
            return false
        }
        if (_placeLong.value!!.isBlank()) {
            _uploadState.value = UploadState.UploadError("Potreban je longitude!")
            return false
        }
        if (_picture.value == null) {
            _uploadState.value = UploadState.UploadError("Potrebna je slika!")
            return false
        }
        return true
    }
}

sealed class UploadState {
    object Idle : UploadState()
    object Success : UploadState()
    class UploadError(val message: String? = null) : UploadState()
}