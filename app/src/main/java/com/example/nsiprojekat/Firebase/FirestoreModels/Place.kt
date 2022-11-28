package com.example.nsiprojekat.Firebase.FirestoreModels

import java.util.UUID

data class Place(
    val id: String? = null,
    val name: String? = null,
    val latitude: String? = null,
    val longitude: String? = null,
    val pictureUrl: String? = null
)
