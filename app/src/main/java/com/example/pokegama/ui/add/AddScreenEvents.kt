package com.example.pokegama.ui.add

sealed class AddScreenEvents {
    data class ShowToast(val message: String) : AddScreenEvents()

    object ShowNoInternetDialog : AddScreenEvents()
}