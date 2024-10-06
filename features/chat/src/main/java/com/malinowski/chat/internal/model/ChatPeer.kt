package com.malinowski.chat.internal.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ChatPeer(
    val name: String,
    val address: String,
) : Parcelable