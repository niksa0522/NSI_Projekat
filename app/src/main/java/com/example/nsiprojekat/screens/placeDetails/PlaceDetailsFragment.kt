package com.example.nsiprojekat.screens.placeDetails

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.example.nsiprojekat.R
import com.example.nsiprojekat.databinding.FragmentPlaceDetailsBinding
import com.example.nsiprojekat.databinding.FragmentPlacesListBinding
import com.example.nsiprojekat.sharedViewModels.PlacesListViewModel


class PlaceDetailsFragment : Fragment() {

    private val viewModel: PlacesListViewModel by activityViewModels()
    private var _binding: FragmentPlaceDetailsBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaceDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (viewModel.selectedPlace != null) {
            binding.tvName.text = viewModel.selectedPlace!!.name
            Glide.with(context!!).load(viewModel.selectedPlace!!.pictureUrl).into(binding.placePictureDetails)
            binding.tvLat.text = viewModel.selectedPlace!!.latitude
            binding.tvLong.text = viewModel.selectedPlace!!.longitude

            if (!viewModel.checkPlaceCreator()) {
                binding.btnEdit.visibility = View.GONE
                binding.btnDelete.visibility = View.GONE
            }
        }
    }



}