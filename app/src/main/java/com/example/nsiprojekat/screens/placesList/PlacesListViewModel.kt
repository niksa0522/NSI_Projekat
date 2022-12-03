package com.example.nsiprojekat.screens.placesList

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class PlacesListViewModel : ViewModel() {
    private val db = Firebase.firestore
    val query = db.collection("places").orderBy("name")

    // TODO: queries for place name and location

}