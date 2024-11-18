package com.malinowski.logs.internal.presentation

sealed class LogEffects {

    class ShowToast(val text: String) : LogEffects()
}