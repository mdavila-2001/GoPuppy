package com.mdavila_2001.gopuppy.ui.views.splash

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mdavila_2001.gopuppy.data.local.TokenManager
import com.mdavila_2001.gopuppy.data.remote.network.RetrofitInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SplashViewModel(application: Application) : AndroidViewModel(application) {
    private val tokenManager = TokenManager(application)

    fun checkSession(onResult: (Boolean, Boolean) -> Unit) {
        viewModelScope.launch {
            val token = tokenManager.getToken.first()
            val isWalker = tokenManager.getRole.first()

            if (!token.isNullOrBlank()) {
                RetrofitInstance.authToken = token
                onResult(true, isWalker)
            } else {
                onResult(false, false)
            }
        }
    }
}