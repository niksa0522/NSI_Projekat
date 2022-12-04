package com.example.nsiprojekat.screens.chatList

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.nsiprojekat.Firebase.RealtimeModels.ChatRequest
import com.example.nsiprojekat.Firebase.RealtimeModels.Friend
import com.example.nsiprojekat.Firebase.RealtimeModels.User
import com.example.nsiprojekat.Models.ChatRequestWithKey
import com.example.nsiprojekat.Models.FriendWithKey
import com.example.nsiprojekat.helpers.LiveDataHelper.minusAssign
import com.example.nsiprojekat.helpers.LiveDataHelper.plusAssign
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.Query
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase

class ChatListViewModel : ViewModel() {

    private val mDatabase = Firebase.database("https://nsi-projekat-default-rtdb.europe-west1.firebasedatabase.app/")
    private val auth = Firebase.auth

    private val _friends = MutableLiveData<MutableList<FriendWithKey>>()
    val friends: LiveData<MutableList<FriendWithKey>> = _friends

    fun getFriends(){
        val uid = auth.uid
        if(uid!=null){
            mDatabase.reference.child("friends").child(uid).addChildEventListener(object:
                ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val friend = snapshot.getValue(Friend::class.java)
                    if(friend!=null)
                        _friends+= FriendWithKey(friend,snapshot.key!!)
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                    //ovo ne moze da se desi
                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
        }
    }
    fun getQuery(): Query {
        val uid = auth.uid
        return mDatabase.reference.child("friends").child(uid!!)
    }
}