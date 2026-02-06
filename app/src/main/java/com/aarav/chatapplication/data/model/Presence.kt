package com.aarav.chatapplication.data.model

import android.media.AudioTimestamp

data class Presence(
    val isOnline: Boolean = false,
    val lastSeen: Long = 0L
)
