package com.example.nsiprojekat.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.nsiprojekat.Firebase.RealtimeModels.ChatRequest
import com.example.nsiprojekat.Models.ChatRequestWithKey
import com.example.nsiprojekat.Models.FriendWithKey
import com.example.nsiprojekat.databinding.ItemFriendBinding
import com.example.nsiprojekat.databinding.ItemRequestBinding

class FriendListAdapter(private val listener:FriendClickInterface) : RecyclerView.Adapter<FriendListAdapter.ViewHolder>() {

    interface FriendClickInterface{
        fun onFriendClicked(friendUid:String)
    }

    private var friends:List<FriendWithKey> = listOf()

    fun setList(newList:List<FriendWithKey>){
        friends=newList
        notifyDataSetChanged()
    }

    class ViewHolder(val binding: ItemFriendBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBinding = ItemFriendBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return FriendListAdapter.ViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.userName.text = friends[position].friend.displayName
        Glide.with(holder.itemView.context).load(friends[position].friend.profilePicURl).into(holder.binding.profilePic)
        holder.binding.root.rootView.setOnClickListener{
            listener.onFriendClicked(friends[position].key)
        }
    }

    override fun getItemCount(): Int {
        return friends.size
    }

}