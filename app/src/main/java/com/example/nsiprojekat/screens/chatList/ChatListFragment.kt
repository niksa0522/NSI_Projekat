package com.example.nsiprojekat.screens.chatList

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.example.nsiprojekat.Adapters.FriendListAdapter
import com.example.nsiprojekat.Firebase.RealtimeModels.Friend
import com.example.nsiprojekat.Models.FriendWithKey
import com.example.nsiprojekat.R
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.firebase.ui.database.SnapshotParser

class ChatListFragment : Fragment(),FriendListAdapter.FriendClickInterface {


    private val viewModel: ChatListViewModel by viewModels()
    private var adapter: FriendListAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d("ChatListFragment","OnCreate")

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        Log.d("ChatListFragment","onCreateView")
        viewModel.getFriends()
        return inflater.inflate(R.layout.fragment_chat_list, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("ChatListFragment","onViewCreated")
        /*viewModel.friends.observe(viewLifecycleOwner){
            if(it.size>0){
                var list = it
                //mozda obrnuti listu
                if(adapter==null){
                    adapter= FriendListAdapter(this)
                }
                adapter!!.setList(list)
                val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerUsers)
                recyclerView.adapter=adapter
            }
        }*/
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerUsers)
        val parser: SnapshotParser<FriendWithKey> = SnapshotParser<FriendWithKey> {
            val friend = it.getValue(Friend::class.java)
            return@SnapshotParser FriendWithKey(friend!!,it.key!!)
        }
        val options = FirebaseRecyclerOptions.Builder<FriendWithKey>().setQuery(
            viewModel.getQuery(),
            parser
        ).setLifecycleOwner(viewLifecycleOwner).build()

        recyclerView.itemAnimator = null

        adapter = FriendListAdapter(options,this)
        recyclerView.adapter = adapter




        //TODO change look of friends and requests screens

    }

    override fun onFriendClicked(friendUid: String) {
        val bundle = Bundle()
        bundle.putString("uid",friendUid)
        findNavController().navigate(R.id.action_nav_chat_to_chatWithFriendFragment,bundle)
        //mislim da nema potreba shared view model, najlakse je da se otvori novi fragment i da se njemu prosledi uid friend-a
    }

    override fun onDestroyView() {
        Log.d("CLF","OnDestroy")
        super.onDestroyView()
    }
}