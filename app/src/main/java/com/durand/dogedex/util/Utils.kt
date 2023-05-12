@file:Suppress("DEPRECATION")

package com.durand.dogedex.util

import android.app.ProgressDialog
import android.content.Context
import android.util.Patterns
import androidx.appcompat.app.AlertDialog

fun isValidEmail(email: String?): Boolean {
    return !email.isNullOrEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
}

fun Context.createLoadingDialog(): ProgressDialog {
    return ProgressDialog(this).apply {
        setMessage("Cargando")
    }

}