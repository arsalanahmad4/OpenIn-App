package com.example.openinapp.util

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer

class MutableAppropriateLiveData<T> : MutableLiveData<T>() {

    private var hasBeenHandled = false

    override fun setValue(value: T) {
        hasBeenHandled = false
        super.setValue(value)
    }

    override fun postValue(value: T) {
        hasBeenHandled = false
        super.postValue(value)
    }

    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        super.observe(owner, Observer { t ->
            if (hasBeenHandled) {
                return@Observer
            }
            hasBeenHandled = true
            observer.onChanged(t)
        })
    }

    fun reset() {
        hasBeenHandled = false
    }
}