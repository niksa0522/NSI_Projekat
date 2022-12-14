package com.example.nsiprojekat.`screens`.login

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.nsiprojekat.R
import com.example.nsiprojekat.sharedViewModels.LoginRegistrationViewModel
import com.example.nsiprojekat.databinding.FragmentLoginBinding
import com.example.nsiprojekat.activites.MainActivity
import com.example.nsiprojekat.helpers.ActionState
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class LoginFragment : Fragment() {

    private val ViewModel: LoginRegistrationViewModel by activityViewModels()
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login,container,false)
        val root = binding?.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        binding.lifecycleOwner = viewLifecycleOwner
        binding.data = ViewModel
        val usernameET = binding.etUsername
        val passwordET = binding.etPassword

        InitData(usernameET,passwordET)

        binding.btnLogin.setOnClickListener{ViewModel.login()}
        binding.tvReg.setOnClickListener{findNavController().navigate(R.id.action_loginFragment_to_registrationFragment)}
        binding.tvForgotPassword.setOnClickListener{ViewModel.changePassword()}

        setAuthStateObserver()
    }

    fun InitData(usernameET: EditText, passwordET: EditText){
        usernameET.setText(ViewModel.email.value ?: "")
        passwordET.setText(ViewModel.password.value ?: "")
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


                val auth = Firebase.auth

                val analytics = Firebase.analytics
                analytics.setUserId(auth.uid)
                val bundle = Bundle()
                bundle.putString(FirebaseAnalytics.Param.METHOD, "firebaseAuth")
                analytics.logEvent(FirebaseAnalytics.Event.LOGIN, bundle)

                i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                activity!!.startActivity(i)
                activity!!.finish()
            }
            else {
                if (state is ActionState.ActionError) {
                    Toast.makeText(view!!.context, state.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
        ViewModel.actionState.observe(viewLifecycleOwner, actionStateObserver)
    }

}