package com.example.nsiprojekat.dialogs

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.example.nsiprojekat.R
import com.example.nsiprojekat.activites.MainActivity
import com.example.nsiprojekat.databinding.FragmentNameDialogBinding
import com.example.nsiprojekat.databinding.FragmentPasswordDialogBinding
import com.example.nsiprojekat.helpers.ActionState
import com.example.nsiprojekat.sharedViewModels.UpdateProfileViewModel
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class PasswordDialogFragment : DialogFragment() {
    private var _binding: FragmentPasswordDialogBinding? = null
    private val binding get() = _binding!!

    private val sharedViewModel: UpdateProfileViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPasswordDialogBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnConfirm.setOnClickListener {
            sharedViewModel.updatePassword(binding.textPassword.text.toString())
        }
        binding.btnCancel.setOnClickListener {
            dismiss()
        }
        setAuthStateObserver()

    }

    private fun setAuthStateObserver() {
        val actionStateObserver = Observer<ActionState> { state ->
            if (state == ActionState.Success) {
                Toast.makeText(view!!.context, "Azuriranje je uspesno", Toast.LENGTH_SHORT).show()
                dismiss()
            } else {
                if (state is ActionState.ActionError) {
                    Toast.makeText(view!!.context, state.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
        sharedViewModel.actionState.observe(viewLifecycleOwner, actionStateObserver)
    }


    override fun onStart() {
        super.onStart()
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        sharedViewModel.actionState.removeObservers(viewLifecycleOwner)
        _binding = null
    }
}