package com.example.messageapp.core

import android.util.Log
import android.view.View
import com.google.android.material.snackbar.Snackbar

fun logD(string: String) = Log.d(ConstVariables.LoggerDebugTag, string)

fun snackBar(view: View, message: String) =
    Snackbar.make(view, message, Snackbar.LENGTH_LONG).show()