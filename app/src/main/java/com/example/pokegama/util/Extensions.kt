package com.example.pokegama.util

import android.content.Context
import android.content.res.Configuration
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
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

fun getBackgroundColor(context: Context, faculty: String): Int {
    return when (faculty) {
        "FT" -> ContextCompat.getColor(context, R.color.blue_400)
        "FK" -> ContextCompat.getColor(context, R.color.blue_500)
        "FKH" -> ContextCompat.getColor(context, R.color.blue_600)
        "FKG" -> ContextCompat.getColor(context, R.color.blue_700)
        "SP" -> ContextCompat.getColor(context, R.color.blue_800)
        "FIB" -> ContextCompat.getColor(context, R.color.blue_900)
        "FH" -> ContextCompat.getColor(context, R.color.purple_400)
        "PA" -> ContextCompat.getColor(context, R.color.purple_500)
        "PET" -> ContextCompat.getColor(context, R.color.purple_600)
        "FPT" -> ContextCompat.getColor(context, R.color.purple_700)
        "PSI" -> ContextCompat.getColor(context, R.color.purple_800)
        "FTP" -> ContextCompat.getColor(context, R.color.purple_900)
        "GEO" -> ContextCompat.getColor(context, R.color.yellow_400)
        "FIL" -> ContextCompat.getColor(context, R.color.yellow_500)
        "FEB" -> ContextCompat.getColor(context, R.color.yellow_600)
        "PAU" -> ContextCompat.getColor(context, R.color.yellow_700)
        "SV" -> ContextCompat.getColor(context, R.color.yellow_800)
        "FKT" -> ContextCompat.getColor(context, R.color.teal_700)
        else -> ContextCompat.getColor(context, R.color.teal_200)
    }
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