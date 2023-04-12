package com.malinowski.diploma.model

data class Message(
    val author: String,
    val text: String,
    val time: String? = null
)