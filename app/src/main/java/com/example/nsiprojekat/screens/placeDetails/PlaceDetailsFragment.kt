package com.example.nsiprojekat.screens.placeDetails

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.nsiprojekat.R
import com.example.nsiprojekat.activites.MainActivity
import com.example.nsiprojekat.databinding.FragmentPlaceDetailsBinding
import com.example.nsiprojekat.databinding.FragmentPlacesListBinding
import com.example.nsiprojekat.helpers.ActionState
import com.example.nsiprojekat.sharedViewModels.AddPlaceViewModel
import com.example.nsiprojekat.sharedViewModels.PlacesListViewModel
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class PlaceDetailsFragment : Fragment() {

    private val viewModel: PlacesListViewModel by activityViewModels()
    private val addPlaceVM: AddPlaceViewModel by activityViewModels()
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

            binding.btnDelete.setOnClickListener { viewModel.deletePlace() }
            binding.btnEdit.setOnClickListener {
                setDataForEdit()
                findNavController().navigate(R.id.action_placeDetailsFragment_to_addPlaceFragment)
            }

            if (!viewModel.checkPlaceCreator()) {
                binding.btnEdit.visibility = View.GONE
                binding.btnDelete.visibility = View.GONE
            }
            initObservers()
        }
    }

    private fun setDataForEdit() {
        addPlaceVM.setPlaceValues(viewModel.selectedPlace!!)
        addPlaceVM.setPicture(binding.placePictureDetails.drawable.toBitmap())
        addPlaceVM.editing = true
        addPlaceVM.pictureUrl = viewModel.selectedPlace!!.pictureUrl!!
        addPlaceVM.placeUid = viewModel.selectedPlace!!.id!!
        addPlaceVM.funUpdatePlace = viewModel.updateSelectedPlace
    }

    private fun initObservers() {
        val deleteStateObserver = Observer<ActionState> { state ->
            if (state == ActionState.Success) {
                Toast.makeText(view!!.context, "Place successfully deleted.", Toast.LENGTH_SHORT).show()
                viewModel.resetDeleteState()
                findNavController().popBackStack()
            }
            else {
                if (state is ActionState.ActionError) {
                    Toast.makeText(view!!.context, state.message, Toast.LENGTH_SHORT).show()
                    viewModel.resetDeleteState()
                }
            }
        }
        viewModel.deleteState.observe(viewLifecycleOwner, deleteStateObserver)
    }
}