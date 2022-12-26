package com.example.nsiprojekat.dialogs

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.Observer
import com.example.nsiprojekat.R
import com.example.nsiprojekat.databinding.FragmentNameDialogBinding
import com.example.nsiprojekat.databinding.FragmentPictureDialogBinding
import com.example.nsiprojekat.helpers.ActionState
import com.example.nsiprojekat.sharedViewModels.UpdateProfileViewModel

class NameDialogFragment : DialogFragment() {
    private var _binding: FragmentNameDialogBinding? = null
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
        _binding = FragmentNameDialogBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedViewModel.resetActionState()
        binding.lifecycleOwner = viewLifecycleOwner
        binding.data = sharedViewModel

/*
        sharedViewModel.fName.observe(viewLifecycleOwner){
            binding.textFName.setText(it)
        }
        sharedViewModel.lName.observe(viewLifecycleOwner){
            binding.textLName.setText(it)
        }
*/
        binding.btnConfirm.setOnClickListener {
            sharedViewModel.updateName()
        }
        binding.btnCancel.setOnClickListener {
            dismiss()
        }
        setAuthStateObserver()

    }
    private fun setAuthStateObserver() {
        val actionStateObserver = Observer<ActionState> { state ->
            if (state == ActionState.Success) {
                Toast.makeText(view!!.context, "Updated successfully", Toast.LENGTH_SHORT).show()
                setFragmentResult("Change Name", Bundle())
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