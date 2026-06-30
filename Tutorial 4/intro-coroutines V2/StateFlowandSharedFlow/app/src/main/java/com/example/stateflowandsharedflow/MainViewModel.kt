package com.example.stateflowandsharedflow

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.reduce
import kotlinx.coroutines.flow.fold
import kotlinx.coroutines.flow.forEach
import kotlinx.coroutines.launch


class MainViewModel: ViewModel() {

    //Part 1. StateFlow
    private val _stateFlow = MutableStateFlow(0)  //We can use ou stateFlow to store the state of our UI
    val stateFlow = _stateFlow.asStateFlow()   //Imutable public version


    //Part 2. SharedFlow is also a hotflow Pausei o video 14m21

       fun incrementCounter () {
        _stateFlow.value++

    }



}