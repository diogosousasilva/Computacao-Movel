package com.example.app

// We use wildcards or explicit imports for the generated classes.
// Note: In an IDE, these imports might show as red until `kapt` runs and generates them.
import com.example.app.MyClassWrapper
import com.dam.DataProcessorExtractor

fun main() {
    // ---- Part 1: @Greeting Processor ----
    val myClass = MyClass()
    val wrappedMyClass = MyClassWrapper(myClass) // Use the wrapper class

    wrappedMyClass.sayHello()
    wrappedMyClass.compute()

    println("-------------------")

    // ---- Part 2: @Extract Processor ----
    val input = "Name: John Address: 123 Street"
    
    // Using the generated DataProcessorExtractor
    val extractor = DataProcessorExtractor(input)
    
    println("Name: \${extractor.getName()}")
    println("Address: \${extractor.getAddress()}")
}
