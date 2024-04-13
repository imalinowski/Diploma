package com.malinowski.diploma.model

data class Message(
    val author: String? = null,
    val text: String,
    val time: String? = null,
    val fromRemote: Boolean = true
)