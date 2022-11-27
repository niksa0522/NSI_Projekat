package com.example.nsiprojekat.helpers

import androidx.lifecycle.MutableLiveData

object LiveDataHelper {
    operator fun <T> MutableLiveData<MutableList<T>>.plusAssign(newValue:T){
        val value = this.value ?: mutableListOf()
        if(!value.contains(newValue))
            value.add(newValue)
        this.value=value
    }
    operator fun <T> MutableLiveData<MutableList<T>>.minusAssign(newValue:T){
        val value = this.value ?: mutableListOf()
        if(value.contains(newValue))
            value.remove(newValue)
        this.value=value
    }
}