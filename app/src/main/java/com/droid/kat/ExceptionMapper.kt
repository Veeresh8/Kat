package com.droid.kat

import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject
import javax.inject.Singleton
import javax.net.ssl.SSLHandshakeException

@Singleton
class ExceptionMapper @Inject constructor() {

    fun getExceptionMessage(throwable: Throwable): String {
        val result = when (throwable) {
            is SocketTimeoutException,
            is UnknownHostException,
            is SSLHandshakeException,
            is ConnectException -> {
                "Please check your network connection"
            }
            else -> {
                throwable.message.toString()
            }
        }

        return result
    }
}