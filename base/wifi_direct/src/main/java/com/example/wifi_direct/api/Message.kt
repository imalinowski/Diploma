package com.example.wifi_direct.api

data class Message(
    val author: String? = null,
    val text: String,
    val time: String? = null,
    val fromRemote: Boolean = true
)