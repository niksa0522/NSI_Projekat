package com.example.nsiprojekat.`screens`.registration

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
import androidx.navigation.fragment.findNavController
import com.example.nsiprojekat.R
import com.example.nsiprojekat.activites.MainActivity
import com.example.nsiprojekat.sharedViewModels.LoginRegistrationViewModel
import com.example.nsiprojekat.databinding.FragmentRegistrationBinding
import com.example.nsiprojekat.helpers.ActionState
import com.example.nsiprojekat.helpers.PermissionHelper
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class RegistrationFragment : Fragment() {

    private val ViewModel: LoginRegistrationViewModel by activityViewModels()
    private var _binding: FragmentRegistrationBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = DataBindingUtil.inflate(inflater,R.layout.fragment_registration,container,false)
        val root = binding.root
        return root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.lifecycleOwner = viewLifecycleOwner
        binding.data = ViewModel

        val usernameET = binding.etUsername
        val passwordET = binding.etPassword
        val fName = binding.etFName
        val lName = binding.etLName

        InitData(usernameET,passwordET,fName,lName)

        binding.btnReg.setOnClickListener{ViewModel.createAccount()}
        binding.tvLogin.setOnClickListener{findNavController().navigate(R.id.action_registrationFragment_to_loginFragment)}
        binding.btnAddPicture.setOnClickListener{takePicture()}
        ViewModel.picture.observe(viewLifecycleOwner) { newPicture ->
            binding.profilePic.setImageBitmap(newPicture)
        }

        setAuthStateObserver()
    }
    fun InitData(usernameET: EditText, passwordET: EditText, FName: EditText, LName: EditText){
        usernameET.setText(ViewModel.email.value ?: "")
        passwordET.setText(ViewModel.password.value ?: "")
        FName.setText(ViewModel.fName.value ?: "")
        LName.setText(ViewModel.lName.value ?: "")
        if(ViewModel.picture.value!=null)
            binding.profilePic.setImageBitmap(ViewModel.picture.value)
    }
    val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result-> if(result.resultCode == Activity.RESULT_OK){
        val data: Intent? = result.data
        val picture: Bitmap = data?.extras?.get("data") as Bitmap
        ViewModel.setPicture(picture)
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

    override fun onDestroyView() {
        super.onDestroyView()
        ViewModel.actionState.removeObservers(viewLifecycleOwner)
        _binding = null
    }

    private fun setAuthStateObserver() {
        val actionStateObserver = Observer<ActionState> { state ->
            if (state == ActionState.Success) {
                val i: Intent = Intent(activity, MainActivity::class.java)
                i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK

                val analytics = Firebase.analytics
                val auth = Firebase.auth
                analytics.setUserId(auth.uid)
                val bundle = Bundle()
                bundle.putString(FirebaseAnalytics.Param.METHOD, "firebaseAuth")
                analytics.logEvent(FirebaseAnalytics.Event.LOGIN, bundle)

                activity!!.startActivity(i)
                activity!!.finish()
            } else {
                if (state is ActionState.ActionError) {
                    Toast.makeText(view!!.context, state.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
        ViewModel.actionState.observe(viewLifecycleOwner, actionStateObserver)
    }

}