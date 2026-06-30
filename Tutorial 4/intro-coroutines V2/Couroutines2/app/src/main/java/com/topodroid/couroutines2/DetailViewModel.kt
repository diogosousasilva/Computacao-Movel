package com.topodroid.couroutines2

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class DetailViewModel : ViewModel() {

    init {
        // 1. viewModelScope: Automatically cancelled when user navigates back
        viewModelScope.launch {
            try {
                while (true) {
                    Log.d("CoroutineDemo", "✅ viewModelScope is running...")
                    delay(1000)
                }
            } finally {
                Log.d("CoroutineDemo", "❌ viewModelScope has STOPPED (onCleared called)")
            }
        }

        // 2. GlobalScope: LEAKS! Continues running even after navigating back
        @OptIn(kotlinx.coroutines.DelicateCoroutinesApi::class)
        GlobalScope.launch {
            while (true) {
                Log.w("CoroutineDemo", "⚠️ GlobalScope is LEAKING! Still running in background...")
                delay(1000)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("CoroutineDemo", "DetailViewModel cleared")
    }
}
