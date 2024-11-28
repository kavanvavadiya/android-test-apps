package com.ihrsachin.sensoreventmanageger.jni_models


interface IObserver<T> {
    fun update(event : T)
}

interface IObservable<T> {
    val observers: ArrayList<IObserver<T>>

    fun add(observer: IObserver<T>) {
        observers.add(observer)
    }

    fun remove(observer: IObserver<T>) {
        observers.remove(observer)
    }

    fun trigger(event : T) {
        observers.forEach { it.update(event) }
    }
}