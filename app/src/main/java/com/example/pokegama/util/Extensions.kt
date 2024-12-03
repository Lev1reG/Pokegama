package com.example.pokegama.util

import android.content.Context
import android.widget.Toast
import androidx.core.content.ContextCompat
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.example.pokegama.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

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
        "FA" -> ContextCompat.getColor(context, R.color.purple_300)
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
        "BIO" -> ContextCompat.getColor(context, R.color.teal_200)
        else -> ContextCompat.getColor(context, R.color.blue_300)
    }
}

fun haversine(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val radius = 6378137
    val lat1Rad = Math.toRadians(lat1)
    val lon1Rad = Math.toRadians(lon1)
    val lat2Rad = Math.toRadians(lat2)
    val lon2Rad = Math.toRadians(lon2)

    val dLat = lat2Rad - lat1Rad
    val dLon = lon2Rad - lon1Rad

    val a = sin(dLat / 2).pow(2) + cos(lat1Rad) * cos(lat2Rad) * sin(dLon / 2).pow(2)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))

    return radius * c
}

@BindingAdapter("formattedDistance")
fun TextView.setFormattedDistance(distance: Double?) {
    distance?.let {
        text = when {
            it >= 1000 -> "(${(it / 1000).roundOff()} km)"
            else -> "(${it.roundOff()}m)"
        }
    }
}


fun formatDistance(distance: Double): String {
    return when {
        distance >= 1000 -> "${(distance / 1000).roundOff()} km"
        else -> "${distance.roundOff()} m"
    }
}



fun Long.getFormattedTime(): String {
    val pattern = "hh:mm a"
    val dateFormat = SimpleDateFormat(pattern)
    return dateFormat.format(this)
}

fun Double.roundOff(): Float {
    val format = DecimalFormat("#.#")
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