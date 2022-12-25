package com.example.nsiprojekat.screens.updateProfile

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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.nsiprojekat.R
import com.example.nsiprojekat.databinding.FragmentUpdateProfileBinding
import com.example.nsiprojekat.helpers.PermissionHelper
import com.example.nsiprojekat.sharedViewModels.UpdateProfileViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class UpdateProfileFragment : Fragment() {

    private val viewModel: UpdateProfileViewModel by activityViewModels()
    private var _binding: FragmentUpdateProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUpdateProfileBinding.inflate(inflater,container,false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth
        Glide.with(this).load(auth.currentUser!!.photoUrl).skipMemoryCache(true).diskCacheStrategy(
            DiskCacheStrategy.NONE).into(binding.imgProfilePic)
        binding.textNameValue.text = auth.currentUser!!.displayName

        binding.btnChangeName.setOnClickListener {
            viewModel.setName()
            findNavController().navigate(R.id.action_updateProfileFragment_to_nameDialogFragment)
        }
        binding.btnChangePassword.setOnClickListener {
            findNavController().navigate(R.id.action_updateProfileFragment_to_passwordDialogFragment)
        }
        binding.imgChangePic.setOnClickListener {
            takePicture()
        }


    }
    val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result-> if(result.resultCode == Activity.RESULT_OK){
        val data: Intent? = result.data
        val picture: Bitmap = data?.extras?.get("data") as Bitmap
        viewModel.setPicture(picture)
        findNavController().navigate(R.id.action_updateProfileFragment_to_pictureDialogFragment)
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

}