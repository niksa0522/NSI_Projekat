package com.example.nsiprojekat.Adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.nsiprojekat.Firebase.FirestoreModels.Place
import com.example.nsiprojekat.databinding.ItemPlaceBinding
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class PlacesAdapter(options: FirestoreRecyclerOptions<Place>, private val listener:ClickInterface)
    : FirestoreRecyclerAdapter<Place, PlacesAdapter.ViewHolder>(
    options
) {
    interface ClickInterface{
        fun onClicked(id:String)
    }

    class ViewHolder(val binding: ItemPlaceBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBinding = ItemPlaceBinding.inflate(LayoutInflater.from(parent.context), parent,false)
        Log.d("PLACES", "Viewholder created")
        return ViewHolder(itemBinding)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int,
        model: Place
    ) {
        Log.d("PLACES", "Viewholder bind")
        holder.binding.userName.text = model.name
        Glide.with(holder.itemView.context).load(model.pictureUrl).into(holder.binding.placePic)
        holder.binding.root.rootView.setOnClickListener{
            listener.onClicked(model.id!!)
        }
    }

}