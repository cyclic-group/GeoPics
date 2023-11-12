package com.example.eduucsbcscs184jionghua_chenjionghua_chengeopics

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LoginViewModel : ViewModel() {
    // TODO: Implement the ViewModel
    private val username = MutableLiveData<String>().apply {
        value = ""
    }
    private val password = MutableLiveData<String>().apply {
        value = ""
    }

    fun getUsername(): MutableLiveData<String> {
        return username
    }

    fun getPassword(): MutableLiveData<String> {
        return password
    }
}