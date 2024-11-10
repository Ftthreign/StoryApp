package com.ftthreign.storyapp.helpers

import android.content.Context
import com.google.android.material.dialog.MaterialAlertDialogBuilder

fun showMaterialDialog(
    context: Context,
    title: String,
    message: String,
    positiveButtonText: String,
    onPositiveClick: (() -> Unit)? = null
) {
    MaterialAlertDialogBuilder(context)
        .setTitle(title)
        .setMessage(message)
        .setPositiveButton(positiveButtonText) { dialog, _ ->
            onPositiveClick?.invoke()
            dialog.dismiss()
        }
        .setCancelable(false)
        .show()
}