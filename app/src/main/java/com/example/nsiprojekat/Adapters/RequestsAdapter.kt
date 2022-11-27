package com.example.nsiprojekat.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.nsiprojekat.Firebase.RealtimeModels.ChatRequest
import com.example.nsiprojekat.Models.ChatRequestWithKey
import com.example.nsiprojekat.databinding.ItemRequestBinding

class RequestsAdapter(private val listener:RequestClickInterface,private val requests:List<ChatRequestWithKey>) : RecyclerView.Adapter<RequestsAdapter.ViewHolder>() {

    interface RequestClickInterface{
        fun onAcceptClicked(chatRequest: ChatRequest,requestKey:String)
        fun onCancelClicked(requestKey:String)
    }

    class ViewHolder(val binding: ItemRequestBinding):RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBinding = ItemRequestBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return RequestsAdapter.ViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.userName.text = requests[position].chatRequest.name
        holder.binding.buttonAccept.setOnClickListener {
            listener.onAcceptClicked(requests[position].chatRequest,requests[position].key)
        }
        holder.binding.buttonDeny.setOnClickListener{
            listener.onCancelClicked(requests[position].key)
        }
    }

    override fun getItemCount(): Int {
        return requests.size
    }

}