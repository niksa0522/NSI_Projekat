package com.example.nsiprojekat.screens.placesFilters

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import com.example.nsiprojekat.R
import com.example.nsiprojekat.databinding.FragmentAddPlaceBinding
import com.example.nsiprojekat.databinding.FragmentPlaceFiltersBinding
import com.example.nsiprojekat.sharedViewModels.AddPlaceViewModel
import com.example.nsiprojekat.sharedViewModels.PlacesListViewModel

class PlaceFiltersFragment : Fragment() {

    private val viewModel: PlacesListViewModel by activityViewModels()
    private var _binding: FragmentPlaceFiltersBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_place_filters, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.data = viewModel

        val placeNameFilterET = binding.etPlaceNameFilter
        val placeDistanceFilterET = binding.etPlaceDistanceFilter
        initData(placeNameFilterET, placeDistanceFilterET)

    }

    private fun initData(placeNameFilterET: EditText, placeDistanceFilterET: EditText) {
        placeNameFilterET.setText(viewModel.placeNameFilter.value ?: "")
        placeDistanceFilterET.setText(viewModel.placeDistanceFilter.value ?: "")
    }

}