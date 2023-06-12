package com.example.openinapp.util

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.provider.ContactsContract
import android.util.Patterns
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat.getSystemService
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*


@SuppressLint("ObsoleteSdkInt")
fun hasInternetConnection(connectivityManager: ConnectivityManager) : Boolean{
    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    } else {
        connectivityManager.activeNetworkInfo?.run {
            return when(type) {
                ConnectivityManager.TYPE_WIFI -> true
                ContactsContract.CommonDataKinds.Email.TYPE_MOBILE -> true
                ConnectivityManager.TYPE_ETHERNET -> true
                else -> false
            }
        }
    }
    return false
}
fun convertTimeStampToDateObject(timeStamp: String?) : Date?{
    val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
    parser.timeZone = TimeZone.getTimeZone("UTC")
    return parser.parse(timeStamp)
}

fun convertTimeStampToReadableTime(timeStamp: String?): String {
    val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
    parser.timeZone = TimeZone.getTimeZone("UTC")
    val formatter = SimpleDateFormat("dd MMM yyyy")
    return formatter.format(parser.parse(timeStamp))
}
fun getGreetingMessage(): String {
    val calendar = Calendar.getInstance()
    return when (calendar.get(Calendar.HOUR_OF_DAY)) {
        in 0..4 -> "Good Night"
        in 5..11 -> "Good Morning"
        in 12..15 -> "Good Afternoon"
        in 16..20 -> "Good Evening"
        else -> "Good Night"
    }
}