package com.example.nsiprojekat.screens.placesList

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.nsiprojekat.Adapters.PlacesAdapter
import com.example.nsiprojekat.Firebase.FirestoreModels.Place
import com.example.nsiprojekat.R
import com.example.nsiprojekat.databinding.FragmentPlacesListBinding
import com.example.nsiprojekat.sharedViewModels.PlacesListViewModel
import com.firebase.ui.firestore.FirestoreRecyclerOptions


class PlacesListFragment : Fragment(), PlacesAdapter.ClickInterface {

    private val viewModel: PlacesListViewModel by activityViewModels()
    private lateinit var options: FirestoreRecyclerOptions<Place>
    private lateinit var adapter: PlacesAdapter

    private var _binding: FragmentPlacesListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlacesListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        options = FirestoreRecyclerOptions.Builder<Place>()
            .setQuery(viewModel.query, Place::class.java)
            .setLifecycleOwner(viewLifecycleOwner)
            .build()

        adapter = PlacesAdapter(options, this)

        if (viewModel.distanceFilterOn.value!! && viewModel.currentLocation != null) {
            adapter.setRadiusFilter(viewModel.placeDistanceFilter.value!!.toDouble(), viewModel.currentLocation!!)
        }
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerPlaces)
        recyclerView.adapter = adapter

        binding.fabFilters.setOnClickListener {
            findNavController().navigate(R.id.action_nav_places_to_placeFiltersFragment)
        }

        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_nav_places_to_addPlaceFragment)
        }
    }

    override fun onClicked(id: String) {
        //Toast.makeText(requireContext(), "Place with id: $id", Toast.LENGTH_SHORT).show()
        val place = adapter.snapshots.find { place -> place.id == id }
        if (place != null) {
            viewModel.selectedPlace = place
            findNavController().navigate(R.id.action_nav_places_to_placeDetailsFragment)
        }
    }
}
