package com.example.academyproject.util

import android.util.Log

class AppLogger(
    private val tag: String
): Logger {

    override fun logD(message: String) {
        Log.d(tag, message)
    }

    override fun logI(message: String) {
        Log.i(tag, message)
    }

    override fun logE(message: String) {
        Log.e(tag, message)
    }

    override fun logW(message: String) {
        Log.w(tag, message)
    }
}