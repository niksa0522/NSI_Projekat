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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import com.example.nsiprojekat.R
import com.example.nsiprojekat.databinding.FragmentAddPlaceBinding
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.nsiprojekat.helpers.PermissionHelper

class AddPlaceFragment : Fragment() {

    private val viewModel: AddPlaceViewModel by viewModels()
    private var _binding: FragmentAddPlaceBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_place,container,false)
        val root = binding?.root
        return root
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
    }

    private fun initData(placeNameET: EditText, placeLatET: EditText, placeLongET: EditText){
        placeNameET.setText(viewModel.placeName.value ?: "")
        placeLatET.setText(viewModel.placeLat.value ?: "")
        placeLongET.setText(viewModel.placeLong.value ?: "")
    }

    val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result-> if(result.resultCode == Activity.RESULT_OK){
        val data: Intent? = result.data
        val picture: Bitmap = data?.extras?.get("data") as Bitmap
        viewModel.setPicture(picture)
    }
    }

    fun takePicture(){
        if(PermissionHelper.isCameraPermissionGranted(requireContext())) {
            val cameraIntent: Intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            resultLauncher.launch(cameraIntent)
        }
        else{
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()){
            isGranted: Boolean->
        if(isGranted){
            takePicture()
        }
    }

    fun selectPlaceLocation() {
        //TODO: figure out how to launch gmaps and get latlong
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}