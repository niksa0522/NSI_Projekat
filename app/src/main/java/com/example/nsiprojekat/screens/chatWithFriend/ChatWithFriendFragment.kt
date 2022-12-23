package com.example.nsiprojekat.screens.chatWithFriend

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nsiprojekat.Adapters.ChatAdapter
import com.example.nsiprojekat.databinding.FragmentChatWithFriendBinding
import com.example.nsiprojekat.helpers.ActionState
import java.io.FileDescriptor
import java.io.IOException

class ChatWithFriendFragment : Fragment(),ChatAdapter.ChatInterface {

    private val viewModel: ChatWithFriendViewModel by viewModels()
    private var _binding: FragmentChatWithFriendBinding?= null
    private val binding get() = _binding!!
    private var adapter: ChatAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChatWithFriendBinding.inflate(inflater,container,false)
        val root: View = binding.root

        val uid = arguments?.getString("uid")
        //val myUid = Firebase.auth.uid
        if(uid!=null /*&& myUid!=null*/)
            viewModel.getChat(uid)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val layoutManager = LinearLayoutManager(context)
        layoutManager.stackFromEnd=true
        binding.recyclerMessages.layoutManager = layoutManager

        viewModel.message.observe(viewLifecycleOwner){
            if(it.size>0){
                var list = it
                //mozda obrnuti listu
                if(adapter==null){


                    adapter= ChatAdapter()
                    binding.recyclerMessages.adapter=adapter
                }
                adapter!!.setList(list)
                binding.recyclerMessages.smoothScrollToPosition(adapter!!.itemCount)
            }
        }

        //TODO prikazi razliku izmedju adaptera za prezentaciju
        /*val options = FirebaseRecyclerOptions.Builder<Message>().setQuery(viewModel.getQuery(),Message::class.java).setLifecycleOwner(viewLifecycleOwner).build()

        binding.recyclerMessages.itemAnimator = null
        adapter = ChatAdapter(options,this)
        binding.recyclerMessages.adapter = adapter*/

        viewModel.name.observe(viewLifecycleOwner){
            (activity as AppCompatActivity).supportActionBar?.title = it
        }
        setAuthStateObserver()

        binding.imageSendPicture.setOnClickListener{
            selectImage()
        }
        binding.buttonSendMessage.setOnClickListener {
            if(binding.messageEditText.text.toString()!=""){
                viewModel.sendMessage(binding.messageEditText.text.toString())
                binding.messageEditText.setText("")
            }
        }
    }
    private fun selectImage(){
        val intent: Intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        resultLauncherGallery.launch(intent)
    }
    val resultLauncherGallery = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val uri: Uri? = data?.data
            if (uri != null) {
                val picture: Bitmap? = uriToBitmap(uri)
                if (picture != null) {
                    viewModel.setPicture(picture,uri.lastPathSegment,)
                }
            }
        }
    }
    //Ovo bi mozda moglo u korutinu ali me mrzi
    private fun uriToBitmap(selectedFileUri: Uri): Bitmap? {
        try {
            val parcelFileDescriptor = requireContext().contentResolver.openFileDescriptor(selectedFileUri, "r")
            val fileDescriptor: FileDescriptor = parcelFileDescriptor!!.fileDescriptor
            val image = BitmapFactory.decodeFileDescriptor(fileDescriptor)
            parcelFileDescriptor.close()
            return image
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    private fun setAuthStateObserver() {
        val actionStateObserver = Observer<ActionState> { state ->
            if (state == ActionState.Success) {
                //malo sam shit iskoristio ovo, ima potencijala za mnogo vise al jbg
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
        viewModel.actionState.removeObservers(viewLifecycleOwner)
        _binding = null
    }

    override fun onMessageAdded() {
        binding.recyclerMessages.smoothScrollToPosition(adapter!!.itemCount)
    }

}