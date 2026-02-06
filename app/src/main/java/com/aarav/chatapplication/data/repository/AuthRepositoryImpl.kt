package com.aarav.chatapplication.data.repository

import android.app.Activity
import android.util.Log
import com.aarav.chatapplication.domain.repository.AuthRepository
import com.aarav.chatapplication.utils.PhoneAuthState
import com.aarav.chatapplication.utils.Result
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.io.IOException
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    val firebaseAuth: FirebaseAuth
) : AuthRepository {
    override suspend fun sendOtp(
        phone: String,
        activity: Activity
    ): Flow<PhoneAuthState> = callbackFlow {
        val callback = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                firebaseAuth.signInWithCredential(p0)
                    .addOnSuccessListener {
                        trySend(PhoneAuthState.Verified(it.user?.uid ?: ""))
                        Log.i("AUTH", "Auto verified")
                        close()
                    }
                    .addOnFailureListener {
                        trySend(PhoneAuthState.Error(it.message ?: "Auth failed"))
                        close()
                    }
            }

            override fun onVerificationFailed(exeption: FirebaseException) {
                trySend(PhoneAuthState.Error(exeption.message ?: "Error while verifying OTP"))
                close()
            }

            override fun onCodeSent(verificationId: String, p1: PhoneAuthProvider.ForceResendingToken) {
                trySend(PhoneAuthState.CodeSent(verificationId))
                close()
            }
        }

        val options = PhoneAuthOptions.Builder(firebaseAuth)
            .setPhoneNumber(phone)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(callback)
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)

        awaitClose {  }
    }

    override suspend fun verifyOtp(
        verificationId: String,
        otp: String
    ): Result<Unit> {
        return try {
            val credential = PhoneAuthProvider.getCredential(verificationId, otp)

            firebaseAuth.signInWithCredential(credential)
                .await()

            Log.i("AUTH", "Auto verified")
            Result.Success(Unit)
        }
        catch (e: IOException) {
            Result.Error(e.message ?: "Failed to verify OTP")
        }
    }

    override fun logout() {
        firebaseAuth.signOut()
    }

    override fun isLoggedIn(): Boolean {
        return firebaseAuth.currentUser != null
    }
}