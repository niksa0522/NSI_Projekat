package com.example.nsiprojekat.screens.chatList

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.example.nsiprojekat.Adapters.FriendListAdapter
import com.example.nsiprojekat.Adapters.RequestsAdapter
import com.example.nsiprojekat.Firebase.RealtimeModels.Friend
import com.example.nsiprojekat.Models.FriendWithKey
import com.example.nsiprojekat.R
import com.example.nsiprojekat.screens.friendsRequests.FriendsRequestsViewModel

class ChatListFragment : Fragment(),FriendListAdapter.FriendClickInterface {


    private val viewModel: ChatListViewModel by viewModels()
    private var adapter: FriendListAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getFriends()
        Log.d("ChatListFragment","OnCreate")

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        viewModel.friends.observe(viewLifecycleOwner){
            if(it.size>0){
                var list = it
                //mozda obrnuti listu
                if(adapter==null){
                    adapter= FriendListAdapter(this)
                    val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerUsers)
                    recyclerView.adapter=adapter
                }
                adapter!!.setList(list)
            }
        }
        //TODO change look of friends and requests screens

    }

    override fun onFriendClicked(friendUid: String) {
        Log.d("OnClickFriend","TODO")
        //mislim da nema potreba shared view model, najlakse je da se otvori novi fragment i da se njemu prosledi uid friend-a
    }
}