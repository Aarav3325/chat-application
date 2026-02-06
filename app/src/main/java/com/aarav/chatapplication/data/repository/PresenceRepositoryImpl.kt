package com.aarav.chatapplication.data.repository

import com.aarav.chatapplication.data.model.Presence
import com.aarav.chatapplication.data.remote.FirebasePaths
import com.aarav.chatapplication.domain.repository.PresenceRepository
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ValueEventListener
import jakarta.inject.Inject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class PresenceRepositoryImpl
@Inject constructor(
    val firebaseDatabase: FirebaseDatabase
) : PresenceRepository {

    val rootRef = firebaseDatabase.reference

    override fun observePresence(userId: String): Flow<Presence> = callbackFlow {
        val ref = rootRef.child(FirebasePaths.presence(userId))

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val presence = snapshot.getValue(Presence::class.java)

                presence?.let {
                    trySend(it)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        ref.addValueEventListener(listener)

        awaitClose { ref.removeEventListener(listener) }
    }

    override fun setupPresence(myUserId: String) {
        val presenceRef = rootRef.child(FirebasePaths.presence(myUserId))
        val connectedRef = rootRef.child("/.info/connected")

        connectedRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val connected = snapshot.getValue(Boolean::class.java) ?: false

                if(connected) {
                    presenceRef.child("online")
                        .setValue(true)

                    presenceRef.onDisconnect().updateChildren(
                        mapOf(
                            "online" to false,
                            "lastSeen" to ServerValue.TIMESTAMP
                        )
                    )
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
}