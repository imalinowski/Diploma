package com.example.edge_entities

data class EdgeDevice(
    val name: String,
    val address: String? = null,
) {
    fun requireAddress(): String {
        return address ?: throw IllegalStateException("no address for device $name")
    }
}