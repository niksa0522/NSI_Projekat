package com.example.nsiprojekat.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.nsiprojekat.Firebase.RealtimeModels.ChatRequest
import com.example.nsiprojekat.Models.ChatRequestWithKey
import com.example.nsiprojekat.Models.FriendWithKey
import com.example.nsiprojekat.databinding.ItemFriendBinding
import com.example.nsiprojekat.databinding.ItemRequestBinding
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions

class FriendListAdapter(val options: FirebaseRecyclerOptions<FriendWithKey>,private val listener:FriendClickInterface) : FirebaseRecyclerAdapter<FriendWithKey,FriendListAdapter.ViewHolder>(options) {

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

    /*override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.userName.text = friends[position].friend.displayName
        Glide.with(holder.itemView.context).load(friends[position].friend.profilePicURl).into(holder.binding.profilePic)
        holder.binding.root.rootView.setOnClickListener{
            listener.onFriendClicked(friends[position].key)
        }
    }

    override fun getItemCount(): Int {
        return friends.size
    }*/

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: FriendWithKey) {
        holder.binding.userName.text = model.friend.displayName
        Glide.with(holder.itemView.context).load(model.friend.profilePicURl).skipMemoryCache(true).diskCacheStrategy(
            DiskCacheStrategy.NONE).into(holder.binding.profilePic)
        holder.binding.root.rootView.setOnClickListener{
            listener.onFriendClicked(model.key)
        }
    }

}