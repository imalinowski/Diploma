package com.malinowski.chat.internal.model

data class Message(
    val author: String? = null,
    val text: String,
    val time: String? = null,
    val fromRemote: Boolean = true
)