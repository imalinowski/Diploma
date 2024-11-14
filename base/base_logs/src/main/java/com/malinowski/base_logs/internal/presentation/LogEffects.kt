package com.malinowski.base_logs.internal.presentation

sealed class LogEffects {

    class ShowToast(val text: String) : LogEffects()
}