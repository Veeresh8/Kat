package com.droid.kat

import android.util.Log

inline fun Any.info(tag: String = "", message: () -> String) {
    if (BuildConfig.DEBUG) {
        Log.i(tag, message.invoke())
    }
}

inline fun Any.error(tag: String = "", message: () -> String) {
    if (BuildConfig.DEBUG) {
        Log.e(tag, message.invoke())
    }
}

inline fun Any.warning(tag: String = "", message: () -> String) {
    if (BuildConfig.DEBUG) {
        Log.w(tag, message.invoke())
    }
}

inline fun Any.debug(tag: String = "", message: () -> String) {
    if (BuildConfig.DEBUG) {
        Log.d(tag, message.invoke())
    }
}