package com.example.nsiprojekat.sharedViewModels

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.text.Editable
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.nsiprojekat.activites.MainActivity
import com.example.nsiprojekat.Firebase.RealtimeModels.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.google.rpc.context.AttributeContext.Auth
import java.io.ByteArrayOutputStream

class LoginRegistrationViewModel : ViewModel() {
    private lateinit var auth: FirebaseAuth

    private val _picture = MutableLiveData<Bitmap>()

    val picture: LiveData<Bitmap> = _picture

    private val _fName = MutableLiveData<String>()

    val fName: LiveData<String> = _fName

    private val _lName = MutableLiveData<String>()

    val lName: LiveData<String> = _lName

    private val _password = MutableLiveData<String>()

    val password: LiveData<String> = _password

    private val _email = MutableLiveData<String>()

    val email: LiveData<String> = _email


    private val _authState = MutableLiveData<AuthState>(AuthState.Idle)
    val authState: LiveData<AuthState> = _authState

    init {
        auth = Firebase.auth
    }

    fun setPicture(picture: Bitmap){
        _picture.value=picture
    }
    fun onFNameTextChanged(p0: Editable?){
        _fName.value = p0.toString()
    }
    fun onLNameTextChanged(p0: Editable?){
        _lName.value = p0.toString()
    }
    fun onEmailTextChanged(p0: Editable?){
        _email.value = p0.toString()
    }
    fun onPasswordTextChanged(p0: Editable?){
        _password.value = p0.toString()
    }


    fun createAccount(){
        if(checkData(false)) {
            val email = "${email.value}"
            auth.createUserWithEmailAndPassword(email, password.value!!)
                .addOnCompleteListener() { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        UploadInfo()
                    } else {
                        _authState.value = AuthState.AuthError("Creation Failed!")
                    }
                }
        }
    }

    fun login(){
        if(checkData(true)) {
            val email = "${email.value}"
            auth.signInWithEmailAndPassword(email, password.value!!)
                .addOnCompleteListener() { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        _authState.value = AuthState.Success
                    } else {
                        _authState.value = AuthState.AuthError("Login Failed!")
                    }
                }
        }
    }
    private fun UploadInfo(){
        val userID: String = auth.currentUser?.uid ?: ""
        if(userID == "")
            _authState.value = AuthState.AuthError("Ovo nije trebalo da se desi!")
        //Prvo se upload-uje slika
        var storage = Firebase.storage
        var imageRef: StorageReference? = storage.reference.child("users").child(userID).child("${email.value}.jpg")
        val baos = ByteArrayOutputStream()
        val bitmap = picture.value
        bitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()
        val uploadTask = imageRef!!.putBytes(data)

        val urlTask = uploadTask.continueWithTask{ task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    val user = auth.currentUser
                    user!!.delete()
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Log.d("SIGNUP", "User account deleted.")
                            }
                        }
                    _authState.value = AuthState.AuthError("Upload error: ${it.message}")
                }
            }
            imageRef.downloadUrl
        }.addOnCompleteListener{task ->
            if(task.isSuccessful){
                //kada je slika uploadovana uzima se njen url i uploaduje se user
                val imageUrl = task.result.toString()
                val user = User(fName.value,lName.value,imageUrl)
                val database = Firebase.database("https://nsi-projekat-default-rtdb.europe-west1.firebasedatabase.app/")
                val userRef = database.reference.child("users").child(userID).setValue(user)
                database.reference.child("emails").child(userID).setValue(email)
                val profileUpdate = userProfileChangeRequest {
                    displayName = "${fName.value} ${lName.value}"
                    photoUri = Uri.parse(imageUrl)
                }

                auth.currentUser!!.updateProfile(profileUpdate)
                _authState.value = AuthState.Success
            }
        }
    }
    private fun checkData(login: Boolean):Boolean{
        if(email.value == null || email.value == "")
        {
            _authState.value = AuthState.AuthError("Unesi Email!")
            return false
        }
        if(password.value == null || password.value == "")
        {
            _authState.value = AuthState.AuthError("Unesi Lozinku!")
            return false
        }
        if(!login){
            if(fName.value == null || fName.value == "")
            {
                _authState.value = AuthState.AuthError("Unesi Ime!")
                return false
            }
            if(lName.value == null || lName.value == "")
            {
                _authState.value = AuthState.AuthError("Unesi Prezime!")
                return false
            }
            if(picture.value == null){
                _authState.value = AuthState.AuthError("Potrebna je slika!")
                return false
            }
        }
        return true
    }
}

sealed class AuthState {
    object Idle : AuthState()
    object Success : AuthState()
    class AuthError(val message: String? = null) : AuthState()
}