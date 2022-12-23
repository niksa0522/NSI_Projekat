package com.example.nsiprojekat.Firebase.FirestoreModels

data class Place(
    val id: String? = null,
    val creatorId: String? = null,
    val name: String? = null,
    val latitude: String? = null,
    val longitude: String? = null,
    val pictureUrl: String? = null,
    val nameQueryList: List<String> = listOf()
)
