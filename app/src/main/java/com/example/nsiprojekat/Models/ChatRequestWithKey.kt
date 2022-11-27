package com.example.nsiprojekat.Models

import com.example.nsiprojekat.Firebase.RealtimeModels.ChatRequest

data class ChatRequestWithKey(val chatRequest: ChatRequest,val key:String){
    override fun equals(other: Any?): Boolean {
        if(other is ChatRequestWithKey){
            return other.key.equals(this.key)
        }
        return super.equals(other)
    }
}
