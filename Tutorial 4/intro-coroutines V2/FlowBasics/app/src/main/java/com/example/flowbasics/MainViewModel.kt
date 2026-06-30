package com.example.flowbasics

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    val TAG: String = "ViewModel"

    //We want to have a flow that starts with an initial value, and then counts douwn every
    //1s, notifying the UI
    val countDownFlow = flow<Int>() { //This is a coroutind - we can call suspend functions within this block
        val startingValue = 10                //This is a cold flow. Cold flow only emits if there are subscribers.
        var currentValue = startingValue
        emit(startingValue)
        while (currentValue > 0) {
            delay(1000)
            currentValue--

            //Now, somehow we want to notify our UI that the value changed... We do this my using emit(...) /Flow collector
            emit(currentValue)
        }
   }

   //init {
   //     collectFlow()
   //}

    //Lets now collect the flow outside of compose. //Assume that we want to be notified about the changes to the countdoun
   // private fun collectFlow() {
        //Collecting the flow is a suspend functions, so lets do it in a coroutine
    //     viewModelScope.launch {
    //        countDownFlow.collect{time ->   //Alternatively, we can also use collectLatest if we don't want to collect all the emissions (case ou routine takes some time to execute...)
                //This way will will collect the latest emission.
    //             delay(1500) //-- in this case, since an emission is comming every second, we will run slower and we could collect only the lattest.
                //Delay will be interrupted and the println will only run when time reaches 0
    //             Log.d(TAG,"The current time is $time")
    //       }
    //    }
    // }













    /*
    init {
        collectFlow()
    }

    //Lets now collect the flow outside of compose. //Assume that we want to be notified about the changes to the countdoun
    private fun collectFlow() {
        //Collecting the flow is a suspend functions, so lets do it in a coroutine
        viewModelScope.launch {
            countDownFlow.collect {time ->   //Alternatively, we can also use collectLatest if we don't want to collect all the emissions (case ou routine takes some time to execute...)
                //This way will will collect the latest emission.
                //delay(1500L) -- in this case, since an emission is comming every second, we will run slower and we could collect only the lattest.
                //Delay will be interrupted and the println will only run when time reaches 0
                println("The current time is $time")
            }
        }
    }
*/
}