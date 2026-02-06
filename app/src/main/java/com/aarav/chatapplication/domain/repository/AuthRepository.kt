package com.aarav.chatapplication.domain.repository

import android.app.Activity
import com.aarav.chatapplication.utils.PhoneAuthState
import com.aarav.chatapplication.utils.Result
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun sendOtp(phone: String, activity: Activity): Flow<PhoneAuthState>
    suspend fun verifyOtp(verificationId: String, otp: String): Result<Unit>
    fun logout()

    fun isLoggedIn(): Boolean
}