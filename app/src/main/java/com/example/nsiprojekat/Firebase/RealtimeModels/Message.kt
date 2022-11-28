package com.example.nsiprojekat.Firebase.RealtimeModels

data class Message(val fromUserId:String? = null, val toUserId:String?=null,val text:String?=null,var messageId:String?=null, val imageUrl:String?=null)
