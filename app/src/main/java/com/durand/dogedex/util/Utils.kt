@file:Suppress("DEPRECATION")

package com.durand.dogedex.util

import android.app.ProgressDialog
import android.content.Context
import android.util.Patterns
import androidx.appcompat.app.AlertDialog

fun Any?.asDouble(): Double = (this as? Number)?.toDouble() ?: 0.0

fun numToDouble(x: Any?): Double = (x as? Number)?.toDouble() ?: 0.0

fun Context.createLoadingDialog(): ProgressDialog {
    return ProgressDialog(this).apply {
        setMessage("Cargando")
    }

}