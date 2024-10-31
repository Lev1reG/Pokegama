package com.example.pokegama.ui.adapter

import android.content.Context
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView

class DropdownAdapter {
    companion object {
        fun setupAdapter(
            context: Context,
            autoCompleteTextView: AutoCompleteTextView,
            stringArray: Array<String>,
            layoutResource: Int
        ) {
            val arrayAdapter = ArrayAdapter(context, layoutResource, stringArray)
            autoCompleteTextView.setAdapter(arrayAdapter)
        }
    }
}
