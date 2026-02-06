package com.aarav.chatapplication.domain.repository

import com.aarav.chatapplication.data.model.Presence
import kotlinx.coroutines.flow.Flow

interface PresenceRepository {
    fun observePresence(userId: String): Flow<Presence>

    fun setupPresence(myUserId: String)
}