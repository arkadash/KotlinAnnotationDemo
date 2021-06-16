package me.arkadash.example

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
annotation class MyFaker (
    val filename: String = "null"
)