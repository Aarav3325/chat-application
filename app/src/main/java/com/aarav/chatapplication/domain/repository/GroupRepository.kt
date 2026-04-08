package com.aarav.chatapplication.domain.repository

import com.aarav.chatapplication.data.model.Group
import com.aarav.chatapplication.utils.Result
import kotlinx.coroutines.flow.Flow

interface GroupRepository {
    suspend fun createGroup(
        name: String,
        creatorId: String,
        memberIds: List<String>
    ): Result<String>

    fun observeGroup(groupId: String): Flow<Group>

    fun observeUserGroups(userId: String): Flow<List<String>>
}
