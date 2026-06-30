package com.example.app

import com.example.app.MyClass
import com.example.app.MyClassWrapper
import com.dam.DataProcessorExtractor

fun main() {
    println("--- Testing @Greeting Processor ---")
    val myClass = MyClass()
    val wrappedMyClass = MyClassWrapper(myClass)
    wrappedMyClass.sayHello()
    wrappedMyClass.compute()

    println("\n--- Testing @Extract Regex Processor ---")
    val input = "Name: John Address: 123 Street"
    val extractor = DataProcessorExtractor(input)
    println("Name: ${extractor.getName()}")
    println("Address: ${extractor.getAddress()}")
}