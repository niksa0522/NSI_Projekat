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
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.Observer
import com.example.nsiprojekat.R
import com.example.nsiprojekat.databinding.FragmentPictureDialogBinding
import com.example.nsiprojekat.helpers.ActionState
import com.example.nsiprojekat.sharedViewModels.UpdateProfileViewModel

class PictureDialogFragment : DialogFragment() {

    private var _binding: FragmentPictureDialogBinding? = null
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
        _binding = FragmentPictureDialogBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedViewModel.resetActionState()
        sharedViewModel.profilePicture.observe(viewLifecycleOwner) {
            binding.imgNewPic.setImageBitmap(it)
        }

        binding.btnConfirm.setOnClickListener {
            sharedViewModel.updatePicture()
        }
        binding.btnCancel.setOnClickListener {
            sharedViewModel.cancelPicture()
            dismiss()
        }
        setAuthStateObserver()
        //malo lose izgleda ali tako je napravljen dialog, pokusao sam da sredim nije mi uspelo

    }


    override fun onStart() {
        super.onStart()
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
    }
    private fun setAuthStateObserver() {
        val actionStateObserver = Observer<ActionState> { state ->
            if (state == ActionState.Success) {
                Toast.makeText(view!!.context, "Updated successfully", Toast.LENGTH_SHORT).show()
                setFragmentResult("Change Picture", Bundle())
                dismiss()
            } else {
                if (state is ActionState.ActionError) {
                    Toast.makeText(view!!.context, state.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
        sharedViewModel.actionState.observe(viewLifecycleOwner, actionStateObserver)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        sharedViewModel.actionState.removeObservers(viewLifecycleOwner)
        _binding = null
    }

}