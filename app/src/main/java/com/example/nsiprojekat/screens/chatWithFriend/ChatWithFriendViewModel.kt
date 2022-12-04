package com.example.nsiprojekat.screens.chatWithFriend

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.nsiprojekat.Adapters.ChatAdapter
import com.example.nsiprojekat.Firebase.RealtimeModels.Friend
import com.example.nsiprojekat.Firebase.RealtimeModels.Message
import com.example.nsiprojekat.helpers.LiveDataHelper.minusAssign
import com.example.nsiprojekat.helpers.LiveDataHelper.plusAssign
import com.example.nsiprojekat.sharedViewModels.AuthState
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.Query
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream

class ChatWithFriendViewModel : ViewModel() {


    private val mDatabase = Firebase.database("https://nsi-projekat-default-rtdb.europe-west1.firebasedatabase.app/")
    private val auth = Firebase.auth

    private val _messages = MutableLiveData<MutableList<Message>>()
    val message: LiveData<MutableList<Message>> = _messages

    private val _name = MutableLiveData<String>()
    val name: LiveData<String> = _name

    private val _authState = MutableLiveData<AuthState>(AuthState.Idle)
    val authState: LiveData<AuthState> = _authState

    private var picture: Bitmap? = null
    private var picturePath: String? = null

    private var friendUid: String?= null
    private var myUid: String? = null




    fun setPicture(newPicture: Bitmap,path:String?,){
        picture=newPicture
        picturePath=path
        sendPicture()
    }

    fun getQuery(): Query {
        return mDatabase.reference.child("messages").child(myUid!!).child(friendUid!!)
    }

    fun getChat(uid:String, myUid:String){
        this.friendUid=uid
        this.myUid = myUid

        if(myUid!=null && friendUid!=null) {

            mDatabase.reference.child("friends").child(myUid!!).child(friendUid!!).get()
                .addOnCompleteListener {
                    val user = it.result.getValue<Friend>()
                    _name.value = user?.displayName
                }
        }
    }

    fun getChat(uid:String){
        this.friendUid=uid
        myUid = auth.uid


        if(myUid!=null && friendUid!=null){

            mDatabase.reference.child("friends").child(myUid!!).child(friendUid!!).get().addOnCompleteListener {
                val user = it.result.getValue<Friend>()
                _name.value = user?.displayName
            }

            mDatabase.reference.child("messages").child(myUid!!).child(friendUid!!).addChildEventListener(object:
                ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val message = snapshot.getValue(Message::class.java)
                    if(message!=null)
                        _messages+= message
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                    //ne znam da li ovo radi, poruke ne mogu da se brisu
                    val message = snapshot.getValue(Message::class.java)
                    if(message!=null)
                        _messages-= message
                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
        }
    }
    fun sendMessage(text:String){
        val myUid = auth.uid
        if(myUid!=null && friendUid!=null) {
            val databaseRef = mDatabase.reference.child("messages").child(myUid).child(friendUid!!).push()
            val msgKey = databaseRef.key
            val message = Message(myUid,friendUid,text,msgKey)
            databaseRef.setValue(message)
            if (msgKey != null) {
                mDatabase.reference.child("messages").child(friendUid!!).child(myUid).child(msgKey).setValue(message)
            }
            _authState.value = AuthState.Success
        }
    }
    private fun sendPicture(){
        val myUid = auth.uid
        if(myUid!=null && friendUid!=null) {
            val databaseRef = mDatabase.reference.child("messages").child(myUid).child(friendUid!!).push()
            val msgKey = databaseRef.key
            if(msgKey==null)
            {
                //ovo ne moze da se desi al ajde
                databaseRef.removeValue()
                return
            }
            var storage = Firebase.storage
            var imageRef: StorageReference? = storage.reference.child("users").child(myUid).child(msgKey).child(picturePath ?: "newPicture.jpg" )
            val baos = ByteArrayOutputStream()
            val bitmap = picture
            bitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val data = baos.toByteArray()
            val uploadTask = imageRef!!.putBytes(data)
            val urlTask = uploadTask.continueWithTask{ task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        _authState.value = AuthState.AuthError("Upload error: ${it.message}")
                    }
                }
                imageRef.downloadUrl
            }.addOnCompleteListener{task ->
                if(task.isSuccessful){
                    //kada je slika uploadovana uzima se njen url i uploaduje se user
                    val photoUrl = task.result.toString()
                    val message = Message(myUid,friendUid, messageId = msgKey, imageUrl = photoUrl)
                    databaseRef.setValue(message)
                    mDatabase.reference.child("messages").child(friendUid!!).child(myUid).child(msgKey).setValue(message)
                    _authState.value = AuthState.Success
                }
            }
        }
    }
}