package com.example.nsiprojekat.screens.friendsRequests

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.nsiprojekat.Firebase.RealtimeModels.ChatRequest
import com.example.nsiprojekat.Firebase.RealtimeModels.Friend
import com.example.nsiprojekat.Models.ChatRequestWithKey
import com.example.nsiprojekat.helpers.LiveDataHelper.minusAssign
import com.example.nsiprojekat.helpers.LiveDataHelper.plusAssign
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase

class FriendsRequestsViewModel : ViewModel() {

    private val mDatabase = Firebase.database("https://nsi-projekat-default-rtdb.europe-west1.firebasedatabase.app/")
    private val auth = Firebase.auth

    private val _requests = MutableLiveData<MutableList<ChatRequestWithKey>>()
    val requests: LiveData<MutableList<ChatRequestWithKey>> = _requests

    private val _requestMessage=MutableLiveData<String>()
    val requestMessage:LiveData<String> = _requestMessage

    fun getRequests(){
        val uid = auth.uid
        if(uid!=null){
            mDatabase.reference.child("chatRequests").child(uid).addChildEventListener(object: ChildEventListener{
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val request = snapshot.getValue(ChatRequest::class.java)
                    if(request!=null)
                        _requests+= ChatRequestWithKey(request,snapshot.key!!)
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                    //ne znam da li ovo radi, samo osoba koja dobije request moze da ga obrise
                    val request = snapshot.getValue(ChatRequest::class.java)
                    if(request!=null)
                        _requests-= ChatRequestWithKey(request,snapshot.key!!)
                }
                //TODO check if requests are deleted and if they can be duped

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
        }
    }

    fun sendRequest(email:String){
        if(email.equals(auth.currentUser!!.email)){
            _requestMessage.value="Uneli ste vasu email adresu"
        }
        mDatabase.reference.child("emails").orderByChild("value").equalTo(email).limitToFirst(1).get().addOnCompleteListener() {
            if(it.isSuccessful){
                val result = it.result.value
                if(result!=null){
                    val hashMap = result as HashMap<*, *>
                    hashMap.forEach { entry ->
                        val uid = entry.key as String
                        if(auth.uid!=null){
                            val requestRef = mDatabase.reference.child("chatRequests").child(uid).child(auth.uid!!)
                                .setValue(ChatRequest(auth.currentUser!!.displayName!!,auth.uid!!,auth.currentUser!!.photoUrl.toString()))
                        }
                        //Mozda ovde isto moze da se posalje poruka
                    }

                }
            }
            else{
                _requestMessage.value="Nema korisnik sa ovom email adresom"
            }
        }
    }

    fun acceptRequest(request:ChatRequest, requestKey:String){
        addFriend(request)
        deleteRequest(requestKey)
        //moze da se preko cloud messaging mozda posalje notifikacija
    }
    fun denyRequest(requestKey:String){
        deleteRequest(requestKey)
        //moze da se preko cloud messaging mozda posalje notifikacija
    }
    private fun addFriend(request:ChatRequest){
        val uid = auth.uid
        if(uid!=null){
            mDatabase.reference.child("friends").child(uid).child(request.uidSender!!).setValue(Friend(request.name,request.profilePicUrl))
            mDatabase.reference.child("friends").child(request.uidSender!!).child(uid).setValue(Friend(auth.currentUser!!.displayName,auth.currentUser!!.photoUrl.toString()))
        }
    }
    private fun deleteRequest(requestKey: String){
        val uid = auth.uid
        if(uid!=null){
            mDatabase.reference.child("chatRequests").child(uid).child(requestKey).removeValue()
        }
    }

}