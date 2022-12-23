package com.example.nsiprojekat.screens.placesAddNew

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.example.nsiprojekat.R
import com.example.nsiprojekat.databinding.FragmentAddPlaceBinding
import androidx.navigation.fragment.findNavController
import com.example.nsiprojekat.helpers.ActionState
import com.example.nsiprojekat.helpers.PermissionHelper
import com.example.nsiprojekat.sharedViewModels.AddPlaceViewModel

class AddPlaceFragment : Fragment() {

    private val viewModel: AddPlaceViewModel by activityViewModels()
    private var _binding: FragmentAddPlaceBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_place, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.data = viewModel
        val placeNameET = binding.etPlaceName
        val placeLatET = binding.etPlaceLat
        val placeLongET = binding.etPlaceLong
        initData(placeNameET, placeLatET, placeLongET)
        binding.btnAddPicture.setOnClickListener{takePicture()}
        viewModel.picture.observe(viewLifecycleOwner) { newPicture ->
            binding.placePic.setImageBitmap(newPicture)
        }
        binding.btnLocation.setOnClickListener { selectPlaceLocation() }
        binding.btnAddPlace.setOnClickListener { viewModel.addPlaceToDB() }

        setUploadStateListener()
    }

    private fun initData(placeNameET: EditText, placeLatET: EditText, placeLongET: EditText) {
        placeNameET.setText(viewModel.placeName.value ?: "")
        placeLatET.setText(viewModel.placeLat.value ?: "")
        placeLongET.setText(viewModel.placeLong.value ?: "")
    }

    private val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            result-> if(result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val picture: Bitmap = data?.extras?.get("data") as Bitmap
                viewModel.setPicture(picture)
            }
    }

    private fun takePicture(){
        if(PermissionHelper.isCameraPermissionGranted(requireContext())) {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            resultLauncher.launch(cameraIntent)
        }
        else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()) {
            isGranted: Boolean->
        if(isGranted) {
            takePicture()
        }
    }

    private fun selectPlaceLocation() {
        findNavController().navigate(R.id.action_addPlaceFragment_to_placesChooseLocationFragment)
    }

    private fun setUploadStateListener() {
        val actionStateObserver = Observer<ActionState> { state ->
            if (state == ActionState.Success) {
                Toast.makeText(view!!.context, "New place has been added.", Toast.LENGTH_SHORT).show()
                viewModel.resetAddPlace()
                findNavController().navigate(R.id.action_addPlaceFragment_to_nav_places)
            } else {
                if (state is ActionState.ActionError) {
                    Toast.makeText(view!!.context, state.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
        viewModel.actionState.observe(viewLifecycleOwner, actionStateObserver)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}