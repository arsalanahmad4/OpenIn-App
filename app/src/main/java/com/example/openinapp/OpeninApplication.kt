package com.example.openinapp

import android.app.Application
import android.content.Context

class OpeninApplication: Application(){

    companion object{
        var appContext: Context? = null
    }
}