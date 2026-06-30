package com.example.coroutines

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import kotlin.system.measureTimeMillis
import kotlin.time.measureTime

class MainActivity : AppCompatActivity() {
    val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        //***********************************************************************************
        // 1)  Starting our First Coroutine - Kotlin Coroutines
        //***********************************************************************************
        //Note: If the main thread is terminated, the coroutine is also terminated
        GlobalScope.launch {
            delay(10000L)   //This function will only pause the coroutine, not the thread
            Log.d(TAG, "Coroutine says hello from ${Thread.currentThread().name}")
        }
        Log.d(TAG, "Hello from ${Thread.currentThread().name}")

        //GlobalScope is a coroutine scope in Kotlin that launches coroutines at the application
        // level, independent of any component lifecycle. It should be used with caution, as coroutines
        // launched in GlobalScope are not canceled automatically, which can lead to memory leaks or
        // unnecessary background work.
        // For that reason, GlobalScope is typically discouraged in favor of lifecycle-aware scopes like
        // viewModelScope, lifecycleScope, or custom CoroutineScopes.
        // When is GlobalScope used?
        // Background jobs that should run for the lifetime of the application such as login, analytics, crash reporting...

        //***********************************************************************************
        // 2)  Suspend Functions - Kotlin Coroutines
        //***********************************************************************************
        //GlobalScope.launch {
        //    delay(3000L) //delay is a suspend function (note the arrow on the left). Suspend functions can only be called from a coroutine or another suspend function or a coroutine.
        //    val networkCallAnswer1 = doNetworkCall()
        //   val networkCallAnswer2 = doNetworkCall2()
            //The coroutine is suspended, up to this point, for the duration of both calls (6s)
        //    Log.d(TAG, networkCallAnswer1)
        //    Log.d(TAG, networkCallAnswer2)
        //}
        //We cannot call doNetworkCall() directly here because it is a suspend function.

        //***********************************************************************************
        // 3)  Coroutine Contexts - Kotlin Coroutines
        //***********************************************************************************
        //Coroutines are alwasy started within a context. The context determines what thread the coroutine will run on.
        //var text = findViewById<TextView>(R.id.text)
        //text.setText("Hello Hello Yuppi")
        //GlobalScope.launch(Dispatchers.IO) { //This coroutine will run on the main thread
        //    //Dispatches.Main is the main thread (You can only change the UI from the main thread)
        //    //Dispatches.IO is used for network and disk operations
        //    //Dispatches.Default is used for CPU intensive operations
        //    //Dispatches.Unconfined is used for coroutines that don't care about the thread they run on
        //    //you can also create your own context --> val myContext = newSingleThreadContext("MyThread")
        //      delay(3000L)
        //      Log.d(TAG, "Coroutine says hello from ${Thread.currentThread().name}")

        //      val networkCallAnswer1 = doNetworkCall() //Execute this call in the IO Dispatcher

            //Now we can easily swithc the context to the main thread
        //     withContext(Dispatchers.Main) {
        //         text.setText(networkCallAnswer1)
                //Acess the UI -- here I can change the UI from the main thread with networkCallAnswer1
        //         Log.d(TAG, "Coroutine says hello from ${Thread.currentThread().name} : " + networkCallAnswer1)
        //     }

        //}

        //***********************************************************************************
        // 3)  runBlocking - Kotlin Coroutines
        //***********************************************************************************
        ////There is a function that will start a coroutine in the current thread, but it will block the main thread until the coroutine is finished.
        //Log.d(TAG, "Before runBlocking")
        //runBlocking { //Here we are already inside a coroutine scope
        //    launch(Dispatchers.IO) {
        //        delay(3000L)
        //        Log.d(TAG, "Finished IO Coroutine 1")
        //    }
        //    launch(Dispatchers.IO) { delay(3000L)
        //        Log.d(TAG, "Finished IO Coroutine 2")
        //    }
            //Both coroutines will be executed in parallel (they will run concurrently) See how much time it takes...

        //    //We can use runBlocking to call suspend functions. It can also be used for testing purposes.
        //      Log.d(TAG, "Start of runBlocking")
        //      delay(5000L)
        //      Log.d(TAG, "End of runBlocking")
        //}
        //Log.d(TAG, "After runBlocking")
        //Almost the same as Thread.sleep...

        //***********************************************************************************
        // 4)  Jobs, Waiting, Cancelation  - Kotlin Coroutines
        //***********************************************************************************
        //Whenever we launch a coroutine, it returns a Job. We can wait for them and cancel them.
        //val job = GlobalScope.launch(Dispatchers.Default) {
            //a) Demonstrating the waiting of a job
            //repeat(5) {
            //       Log.d(TAG, "Coroutine is still working...")
            //       delay(1000L)
            // }

            //b, showing that cancelation is cooperative
            //     Log.d(TAG, "Starting long running calculation...")
        //    for (i in 30..50) {
        //       if (isActive) {  //We have to check if the job is still active and was not canceled
        //          Log.d(TAG, "Result for i = $i: ${fib(i)}")
        //       }
        //    }
        //    Log.d(TAG, "Ending long running calculation...")

            //C. We can also cancel a coroutine if it takes too long, with a timeout.
        //  withTimeout(4000L) {
        //       Log.d(TAG, "Starting long running calculation...")
        //       for (i in 30..40) {
                   // if (isActive) {  //We have to check if the job is still active and was not canceled
        //               Log.d(TAG, "Result for i = $i: ${fib(i)}")
                   //}
        //      }
            //    Log.d(TAG, "Ending long running calculation...")
        //}


        //}
        //We can wait for a jobt to finish, using the join() function that is a suspend function.
        //therefore, we need to call it from a coroutine scope.
    //runBlocking {
            //job.join() //a) It will block the  thread until the job is finished

            //b) We can also cancel the job using the cancel() function.
    //        delay(2000L)
    //        job.cancel()
            //Log.d(TAG, "Canceled the job!")
            //Note. Cancel is coorperative. The job will be cancelled, but the coroutine will still be running. It needs
            //to be notified that it was canceled, by checking the isActive flag.or by calling delay(...)

    //        Log.d(TAG, "Main thread is continuing...")
    //    }


        //***********************************************************************************
        // 5)  Async and Await  - Kotlin Coroutines
        //***********************************************************************************
        //If we want to return a value from a coroutine, we can use the async and await functions.

        //Imagine we want to execute two network calls in parallel. We can do it with the async and await functions.
    //GlobalScope.launch(Dispatchers.IO) {

            //a) This way, we will execcute both calls in sequence (it will take 6 seconds to finish)
            //val answer1 = doNetworkCall()
            //val answer2 = doNetworkCall2()

          // Log.d(TAG, "Answer1 is: $answer1")
          //  Log.d(TAG, "Answer2 is: $answer2")

            //b) We can execute both function calls asynchronously. It will take only 3 seconds to finish.
            //Kotlin has a cool function to measure time
        //val time = measureTimeMillis {

              // val answer1 = doNetworkCall()
                // val answer2 = doNetworkCall2()

                //Lets do it asynchronously
               //var answer1: String? = null
               //var answer2: String? = null

                //var job1 = launch {  answer1 = doNetworkCall()  }
                //var job2 = launch {  answer2 = doNetworkCall2() }

                //Wait for both coroutines to finish or else bother answer1 and answer2 will be null
                //job1.join()
                //job2.join()

                //Log.d(TAG, "Answer1 is: $answer1")
                //Log.d(TAG, "Answer2 is: $answer2")

                //}
                //Log.d(TAG,"Request took $time ms.") //Now the requests will take only 3 seconds to finish

            //c. There is a better way to do it...
            // using async... it will also launch a coroutine
                //val time = measureTimeMillis {

                //val answer1 = async { doNetworkCall()  } //These answers are not jobs. They are the Deferred of a String Deferred<String>
                //val answer2 = async { doNetworkCall2() }

                // Log.d(TAG, "Answer1 is: ${answer1.await()}")
                //Log.d(TAG, "Answer2 is: ${answer2.await()}")
                //}
                // Log.d(TAG,"Request took $time ms.") //Now the requests will take only 3 seconds to finish

                //}

    }
}

/*
suspend fun doNetworkCall() : String {
    delay(3000L)
    return "This is the answer 1"
}

suspend fun doNetworkCall2() : String {
    delay(3000L)
    return "This is the answer 2"
}

//Calculate the fibonacci sequence
fun fib(n: Int): Long {
    return if (n == 0) 0
    else if (n == 1) 1
    else fib(n - 1) + fib(n - 2)
}
*/