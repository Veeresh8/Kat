package com.droid.kat

import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast

fun View.gone() {
    visibility = View.GONE
}

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.isVisible(): Boolean {
    return visibility == View.VISIBLE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun Any.toast(message: String?) {
    Handler(Looper.getMainLooper()).post {
        Toast.makeText(App.instance, message, Toast.LENGTH_SHORT).show()
    }
}

fun Any.toastLong(message: String?) {
    Handler(Looper.getMainLooper()).post {
        Toast.makeText(App.instance, message, Toast.LENGTH_LONG).show()
    }
}
