package com.example.nsiprojekat.screens.placesList

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.example.nsiprojekat.Adapters.PlacesAdapter
import com.example.nsiprojekat.Firebase.FirestoreModels.Place
import com.example.nsiprojekat.R
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.firebase.ui.firestore.SnapshotParser


class PlacesListFragment : Fragment(), PlacesAdapter.ClickInterface {

    private val viewModel: PlacesListViewModel by viewModels()
    private lateinit var options: FirestoreRecyclerOptions<Place>
    private lateinit var adapter: PlacesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_places_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        options = FirestoreRecyclerOptions.Builder<Place>()
            .setQuery(viewModel.query, Place::class.java)
            .setLifecycleOwner(viewLifecycleOwner)
            .build()

        adapter = PlacesAdapter(options, this)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerPlaces)
        recyclerView.adapter = adapter

        Log.d("PLACES", "Place count: ${adapter.snapshots.size}")
    }

    override fun onClicked(id: String) {
        Toast.makeText(requireContext(), "Place with id: $id", Toast.LENGTH_SHORT).show()
    }
}