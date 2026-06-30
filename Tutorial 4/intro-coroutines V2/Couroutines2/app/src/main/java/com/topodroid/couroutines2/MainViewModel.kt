package com.topodroid.couroutines2

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    private val _timerValue = MutableStateFlow(0)
    val timerValue: StateFlow<Int> = _timerValue

    init {
        viewModelScope.launch {
            while (true) {
                delay(1000)
                _timerValue.value++
            }
        }
    }
}
