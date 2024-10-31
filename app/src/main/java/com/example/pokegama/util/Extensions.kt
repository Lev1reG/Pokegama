package com.example.pokegama.util

import android.content.Context
import android.content.res.Configuration
import android.view.View
import android.widget.Toast
import androidx.core.view.ViewCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.example.pokegama.R
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

fun Context.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Long.getFormattedTime(): String {
    val pattern = "hh:mm a"
    val dateFormat = SimpleDateFormat(pattern)
    return dateFormat.format(this)
}

fun Float.roundOff(): Float {
    val format = DecimalFormat("#.##")
    format.roundingMode = RoundingMode.CEILING
    return format.format(this).toFloat()
}

fun Context.showDialog(
    title: String,
    message: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val dialog = MaterialAlertDialogBuilder(this)
        .setBackground(getDrawable(R.drawable.alert_dialog_bg))
        .setTitle(title)
        .setMessage(message)
        .setPositiveButton("Confirm") { _, _ ->
            onConfirm()
        }
        .setNegativeButton("Cancel", null)
        .create()
    dialog.setOnDismissListener {
        onDismiss()
    }
    dialog.show()
}