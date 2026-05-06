package com.artssing.androidmodernexampleapp.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(): ViewModel() {
    var username by mutableStateOf("")
        private set

    var password by mutableStateOf("")
        private set

    var isLoading by mutableStateOf(false)
        private set

    var isLoggedIn by mutableStateOf(false)
        private set

    fun onUsernameChange(newName: String) { username = newName }
    fun onPasswordChange(newPass: String) { password = newPass }

    fun login() {
        viewModelScope.launch {
            isLoading = true
            delay(2000) // 模擬網路請求 [8]
            isLoading = false
            isLoggedIn = true
        }
    }
}