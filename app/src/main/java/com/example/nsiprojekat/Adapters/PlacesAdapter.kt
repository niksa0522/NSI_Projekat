package com.example.nsiprojekat.Adapters

import android.location.Location
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.nsiprojekat.Firebase.FirestoreModels.Place
import com.example.nsiprojekat.databinding.ItemPlaceBinding
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.gms.maps.model.LatLng

class PlacesAdapter(options: FirestoreRecyclerOptions<Place>, private val listener:ClickInterface)
    : FirestoreRecyclerAdapter<Place, PlacesAdapter.ViewHolder>(
    options
) {
    private var radius = 0.0
    private var shouldTrimDistance = false
    private var placesList: List<Place> = listOf()
    private var location: LatLng? = null

    interface ClickInterface{
        fun onClicked(id:String)
    }

    class ViewHolder(val binding: ItemPlaceBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBinding = ItemPlaceBinding.inflate(LayoutInflater.from(parent.context), parent,false)
        return ViewHolder(itemBinding)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int,
        model: Place
    ) {
        var modelToUse: Place? = model
        if (shouldTrimDistance && placesList.isNotEmpty()) {
            modelToUse = placesList.find { place -> place.id == model.id }
            if (modelToUse != null){
                holder.binding.userName.text = modelToUse.name
                Glide.with(holder.itemView.context).load(modelToUse.pictureUrl).into(holder.binding.placePic)
                holder.binding.root.rootView.setOnClickListener {
                    listener.onClicked(modelToUse.id!!)
                }
            }
            else {
                holder.itemView.visibility = View.GONE
                holder.itemView.layoutParams = RecyclerView.LayoutParams(0, 0)
            }
        }
        else {
            holder.binding.userName.text = modelToUse!!.name
            Glide.with(holder.itemView.context).load(modelToUse.pictureUrl).into(holder.binding.placePic)
            holder.binding.root.rootView.setOnClickListener {
                listener.onClicked(modelToUse.id!!)
            }
            holder.itemView.visibility = View.VISIBLE
            holder.itemView.layoutParams =
                RecyclerView.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
        }
    }

    override fun onDataChanged() {
        super.onDataChanged()
        if (shouldTrimDistance) {
            placesList = snapshots.toList()
            if (location != null) {
                placesList = trimByDistance(placesList, location!!, radius)
            }
        }
    }

    fun setRadiusFilter(r: Double, loc: LatLng) {
        radius = r
        location = loc
        shouldTrimDistance = true
        placesList = snapshots.toList()
    }

    fun resetRadiusFilter() {
        shouldTrimDistance = false
    }

    private fun trimByDistance(list: List<Place>, currentLocation: LatLng, distance: Double): List<Place> {
        val newList = list.filter { place ->
            measureDistance(place, currentLocation) <= distance
        }
        return newList
    }

    private fun measureDistance(place: Place, currentLocation: LatLng): Float {
        val results = FloatArray(1)
        Location.distanceBetween(
            currentLocation.latitude,
            currentLocation.longitude,
            place.latitude!!.toDouble(),
            place.longitude!!.toDouble(),
            results
        )
        return results[0]
    }


}