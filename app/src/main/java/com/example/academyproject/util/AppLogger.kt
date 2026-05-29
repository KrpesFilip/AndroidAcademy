package com.example.academyproject.util

import android.util.Log

class AppLogger(
    private val tag: String
) {

    fun logD(message: String) {
        Log.d(tag, message)
    }

    fun logI(message: String) {
        Log.i(tag, message)
    }

    fun logE(message: String) {
        Log.e(tag, message)
    }

    fun logW(message: String) {
        Log.w(tag, message)
    }
}