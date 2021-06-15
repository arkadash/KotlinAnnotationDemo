package me.arkadash.example

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FIELD, AnnotationTarget.FUNCTION)
annotation class MyConstant (
    val propName: String,
    val propValue: String
)