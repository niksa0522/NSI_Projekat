package com.example.nsiprojekat.sharedViewModels

import android.app.Activity
import android.graphics.Bitmap
import android.net.Uri
import android.text.Editable
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.nsiprojekat.Firebase.RealtimeModels.Friend
import com.example.nsiprojekat.Firebase.RealtimeModels.Message
import com.example.nsiprojekat.Firebase.RealtimeModels.User
import com.example.nsiprojekat.Models.FriendWithKey
import com.example.nsiprojekat.helpers.ActionState
import com.example.nsiprojekat.helpers.LiveDataHelper.plusAssign
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream

class UpdateProfileViewModel : ViewModel() {

    private var auth: FirebaseAuth

    private val _profilePicture = MutableLiveData<Bitmap>()

    val profilePicture: LiveData<Bitmap> = _profilePicture

    val fName = MutableLiveData<String>()
    val lName = MutableLiveData<String>()


    private var userPhotoUrl :String? = ""

    private val _actionState = MutableLiveData<ActionState>(ActionState.Idle)
    val actionState: LiveData<ActionState> = _actionState



    init {
        auth = Firebase.auth
    }

    fun resetActionState(){
        _actionState.value = ActionState.Idle
    }

    fun setName(){
        val userID = auth.uid ?: return
        val database = Firebase.database("https://nsi-projekat-default-rtdb.europe-west1.firebasedatabase.app/")
        database.reference.child("users").child(userID).get().addOnSuccessListener {
            val user = it.getValue(User::class.java)
            if(user!=null){
                fName.value=user.fname
                lName.value=user.lname
                userPhotoUrl=user.imageUrl
            }
        }
    }

    fun setPicture(picture: Bitmap){
        _profilePicture.value=picture
    }

    fun updateName(){
        val name = auth.currentUser?.displayName ?: return
        val nameEntered = "${fName.value} ${lName.value}"
        if(name == nameEntered)
            return
        val profileUpdate = userProfileChangeRequest {
            displayName = "${fName.value} ${lName.value}"
        }

        val uid = auth.currentUser?.uid!!
        val database = Firebase.database("https://nsi-projekat-default-rtdb.europe-west1.firebasedatabase.app/")
        val user = User(fName.value,lName.value,userPhotoUrl)
        database.reference.child("users").child(uid).setValue(user)
        auth.currentUser!!.updateProfile(profileUpdate).addOnCompleteListener {
            _actionState.value = ActionState.Success
        }
        updateFriendsInfo(database,uid)
    }
    private fun updateFriendsInfo(mDatabase:FirebaseDatabase,uid:String){
        val friend = Friend("${fName.value} ${lName.value}",auth.currentUser!!.photoUrl.toString())
        mDatabase.reference.child("friends").child(uid).addChildEventListener(object:
            ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val friendKey = snapshot.key!!
                mDatabase.reference.child("friends").child(friendKey).child(uid).setValue(friend)
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
    fun updatePassword(password:String){
        if(password!=""){
            auth.currentUser?.updatePassword(password)?.addOnCompleteListener {
                if(it.isSuccessful){
                    _actionState.value = ActionState.Success
                }
                else{
                    _actionState.value = ActionState.ActionError(it.exception?.message)
                }
            }
        }
        else{
            _actionState.value = ActionState.ActionError("Lozinka ne sme biti prazna")
        }

    }
    fun updatePicture(){
        val userID: String = auth.currentUser?.uid ?: ""
        val email: String = auth.currentUser?.email ?: ""
        if(userID=="" || email=="")
            return
        val storage = Firebase.storage
        val imageRef: StorageReference? = storage.reference.child("users").child(userID).child("${email}.jpg")
        val baos = ByteArrayOutputStream()
        val bitmap = profilePicture.value
        bitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()
        val uploadTask = imageRef!!.putBytes(data)

        val urlTask = uploadTask.continueWithTask{ task ->
            if (!task.isSuccessful) {
                //error
            }
            imageRef.downloadUrl
        }.addOnCompleteListener{task ->
            if(task.isSuccessful){
                _actionState.value = ActionState.Success
            }
        }
    }
    fun cancelPicture(){
        _profilePicture.value=null
    }


}