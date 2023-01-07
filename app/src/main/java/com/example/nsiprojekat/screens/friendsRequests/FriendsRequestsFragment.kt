package com.example.nsiprojekat.screens.friendsRequests

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.example.nsiprojekat.Adapters.RequestsAdapter
import com.example.nsiprojekat.Firebase.RealtimeModels.ChatRequest
import com.example.nsiprojekat.R
import com.example.nsiprojekat.databinding.FragmentChatMainBinding
import com.example.nsiprojekat.databinding.FragmentFriendsRequestsBinding

class FriendsRequestsFragment : Fragment(),RequestsAdapter.RequestClickInterface {

    //Ovde moze da se radi ovako, jer se onCreate zove kada se fragmenti naprave u ChatMain
    //Ovako samo kada se upali ChatMainFragment poziva se onCreateView i tek se tada cita baza
    //Takodje sve dok se nalazimo na ChatMain fragmentu dva child fragmenta zive
    private val viewModel: FriendsRequestsViewModel by viewModels()
    private var _binding: FragmentFriendsRequestsBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: RequestsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFriendsRequestsBinding.inflate(inflater,container,false)
        val root: View = binding.root
        Log.d("FriendsRequestsFragment","onCreateView")
        viewModel.getRequests()
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getFriends()

        viewModel.requests.observe(viewLifecycleOwner){
            if(it.size>0){
                var list = it
                //mozda obrnuti listu
                adapter= RequestsAdapter(this,list)
                binding.recyclerRequests.adapter=adapter
            }
            else{
                adapter= RequestsAdapter(this, listOf())
                binding.recyclerRequests.adapter=adapter
            }
        }
        viewModel.requestMessage.observe(viewLifecycleOwner){
            Toast.makeText(requireContext(),it,Toast.LENGTH_SHORT).show()
            viewModel.resetMessage()
        }
        binding.buttonSendRequest.setOnClickListener {
            if(binding.etEmail.text.toString()!=""){
                viewModel.sendRequest(binding.etEmail.text.toString())
            }
            else{
                Toast.makeText(requireContext(),"Enter email!",Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun onAcceptClicked(chatRequest: ChatRequest, requestKey: String) {
        //call vm function
        viewModel.acceptRequest(chatRequest,requestKey)
    }

    override fun onCancelClicked(requestKey: String) {
        //call vm function
        viewModel.denyRequest(requestKey)
    }

}