package com.example.nsiprojekat.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.nsiprojekat.Firebase.RealtimeModels.Message
import com.example.nsiprojekat.databinding.ItemMessageBinding
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ChatAdapter(/*options:FirebaseRecyclerOptions<Message>,val listener:ChatInterface*/): RecyclerView.Adapter<ChatAdapter.ViewHolder>() {

    interface ChatInterface{
        fun onMessageAdded()
    }
    private var messages:List<Message> = listOf()
    private val auth = Firebase.auth


    fun setList(newList:List<Message>){
        messages=newList
        notifyDataSetChanged()
    }

    class ViewHolder(val binding: ItemMessageBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBinding = ItemMessageBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ChatAdapter.ViewHolder(itemBinding)
    }

    /*override fun onDataChanged() {
        super.onDataChanged()

        listener.onMessageAdded()
    }*/

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.binding.textReceiver.visibility= View.GONE
        holder.binding.imageViewReceiverPicture.visibility= View.GONE
        holder.binding.imageViewSenderPicture.visibility= View.GONE
        holder.binding.textSender.visibility= View.GONE

        val ownId = auth.uid
        val message = messages[position]
        val fromUserId = message.fromUserId


        if(message.text!=null && message.text != ""){
            if(ownId.equals(fromUserId)){
                holder.binding.textSender.visibility= View.VISIBLE
                holder.binding.textSender.text = message.text
            }
            else{
                holder.binding.textReceiver.visibility= View.VISIBLE
                holder.binding.textReceiver.text = message.text
            }
        }
        else{
            if(ownId.equals(fromUserId)){
                Glide.with(holder.itemView.context).load(message.imageUrl).into(holder.binding.imageViewSenderPicture)
                holder.binding.imageViewSenderPicture.visibility= View.VISIBLE
            }
            else{
                Glide.with(holder.itemView.context).load(message.imageUrl).into(holder.binding.imageViewReceiverPicture)
                holder.binding.imageViewReceiverPicture.visibility= View.VISIBLE
            }
        }
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    /*override fun onBindViewHolder(holder: ViewHolder, position: Int, model: Message) {
        holder.binding.textReceiver.visibility = View.GONE
        holder.binding.imageViewReceiverPicture.visibility = View.GONE
        holder.binding.imageViewSenderPicture.visibility = View.GONE
        holder.binding.textSender.visibility = View.GONE

        val ownId = auth.uid
        val fromUserId = model.fromUserId


        if (model.text != null && model.text != "") {
            if (ownId.equals(fromUserId)) {
                holder.binding.textSender.visibility = View.VISIBLE
                holder.binding.textSender.text = model.text
            } else {
                holder.binding.textReceiver.visibility = View.VISIBLE
                holder.binding.textReceiver.text = model.text
            }
        } else {
            if (ownId.equals(fromUserId)) {
                Glide.with(holder.itemView.context).load(model.imageUrl)
                    .into(holder.binding.imageViewSenderPicture)
                holder.binding.imageViewSenderPicture.visibility = View.VISIBLE
            } else {
                Glide.with(holder.itemView.context).load(model.imageUrl)
                    .into(holder.binding.imageViewReceiverPicture)
                holder.binding.imageViewReceiverPicture.visibility = View.VISIBLE
            }
        }
    }*/
}