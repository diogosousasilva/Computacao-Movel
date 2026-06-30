package com.example.flowoperators

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asFlow
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
import kotlin.concurrent.timer


class MainViewModel : ViewModel() {

    val TAG: String = "ViewModel"

    //We want to have a flow that starts with an initial value, and then counts down every
    //1s, notifying the UI
    val countDownFlow =
        flow<Int>() { //This is a coroutind - we can call suspend functions within this block
            val startingValue = 10
            var currentValue = startingValue
            emit(startingValue)
            while (currentValue > 0) {
                delay(1000)
                currentValue--

                //Now, somehow we want to notify our UI that the value changed... We do this my using emit(...) /Flow collector
                emit(currentValue)
            }
        }

    init {
        collectFlow()
    }

    //Lets now collect the flow outside of compose. //Assume that we want to be notified about the changes to the countdoun
    private fun collectFlow() {

        //viewModelScope.launch {
        //    countDownFlow.collectLatest { time ->
        //        delay(1500L);
        //        println("The current time is $time")
        //    }
        //}



        //countDownFlow.onEach { //We can also use onEach to collect the flow
        //    Log.d(TAG,"BingBangBum on $it seconds")
        //}.launchIn(viewModelScope)  //This would be the same as collect, inside a coroutine scope





        //Collecting the flow is a suspend functions, so lets do it in a coroutine
        viewModelScope.launch {

            val count = countDownFlow
              .filter { time ->                             //We can chain several flow operators this way:
                  time % 2 == 0
              }
              .map { time ->
                 time * time
              }
              .onEach { time ->
                 Log.d(TAG,"Just using time as: $time")
              }                                            //On collect we finish the flow
            .collect { time ->   //Alternatively, we can also use collectLatest if we don't want to collect all the emissions (case ou routine takes some time to execute...)
            //  //This way will will collect the latest emission.
            //   delay(500L)   //-- in this case, since an emission is comming every second, we will run slower and we could collect only the lattest.
                //Delay will be interrupted and the println will only run when time reaches 0
            //   Log.d(TAG,"The current time is $time")
            }
            //2. Instead of .collect, we can also use another terminal operator.
        //   .count {    //Terminal flow operator terminate the flow and return a value.
                //It will count the values that mach a specific condition.
        //        Log.d(TAG,"Counting $it")
        //       it % 2 == 0  //If the emission is an even number it will count these. It will then return it as an intenger val
        //     }
        //    Log.d(TAG,"Count value is: $count") //The flow has to finish first

        }



        //3. Another terminal operator : reduce
        //viewModelScope.launch {
        //    val reduceResult = countDownFlow
        //        .reduce { accumulator, value ->
        //            accumulator + value
        //        }
        //    Log.d(TAG,"The reduce Redult is $reduceResult")
        //}


        //4. The same as reduce, we have another terminal function called fold... The same as
        //reduce but lets me set the initial value for the accumulator

       //  viewModelScope.launch {
        //     val reduceResult = countDownFlow
        //        .fold(100) { accumulator, value ->
        //            accumulator + value
        //        }
        //        Log.d(TAG,"The reduce Redult is $reduceResult")
        //}



        //5. Assume we have a list of lists and want to "flatten" that list and create a single list
        //[ [1, 2], [1, 2, 3] ] --> [1,2,1,2,3]
        //In flows we have something similar: Instead of "flattening" lists, we flatten flows....
        //Lets create a flow...

        //val flow1 = flow {
        //    emit(1)
        //    delay(500)
        //    emit(2)
        // }
        //viewModelScope.launch {
        //    flow1.flatMapConcat {value ->   //flatMapMerge will do the same, but in parallel
        //        flow {  //Create another flow
        //            emit(value + 1)
        //            delay(500)
        //            emit(value + 2)
        //        }
        //    }.collect {value ->   //Lets collect the flow
        //        Log.d(TAG,"The value is $value")
        //    }
        //}

        //Application, imagine you have an app the manages recipes. Some of the recipes are emitted as
        //a flow coming from you local database, while others will take longer and will come from an external API.
        //You can flatten this way and combine both sources of recipes.



        //6.Other ways of combining....
        /*
        val flow1 = flow{
            emit("a")
            delay(100)
            emit("b")
        }
        viewModelScope.launch {
            flow1.flatMapMerge { value ->  //If the flow emits another value, stop this flow and process the new value
                 flow {
                    emit(value)
                    delay(2000)
                    emit(value + "_last")
                }
            }.collect {value ->
                Log.d(TAG, "The value is $value")

            }
        }
        */

        //.7 Example, Want to order and apetizer, a main dish followed by a desert. Lets try to simulate
        //that with a flow
/*
        val flow1 = flow {
            delay(250)
            emit("apetizer")
            delay(1000)
            emit("Main dish")
            delay(100)
            emit("Desert")
        }
        viewModelScope.launch {
            flow1.onEach {
                Log.d(TAG,"Flow, $it is delivered")
            }
            .buffer()  //Buffer will make sure that collect runs in a different coroutine
                //    .conflate() //If there are two emissions from the flow that we cannot collect yet, when we finish it we will go directy to the latest
                //emission and drop all the other ones.
            .collect{ currentDish ->                            //We are blocking the flow. The flow will only continue emitting the values
                //if the we finish collecting the previous ones. We can decouple by using a buffer (coroutine scope)
                Log.d(TAG,"Eating: $currentDish")
                delay(1500L)
                 Log.d(TAG,"Finish eating $currentDish")
            }
        }
        */

        //}
        }

}








