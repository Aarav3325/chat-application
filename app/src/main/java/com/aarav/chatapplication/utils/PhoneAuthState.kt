package com.aarav.chatapplication.utils

import com.aarav.chatapplication.domain.model.Message

sealed interface PhoneAuthState {
    data class CodeSent(val verificationId: String): PhoneAuthState
    data class Verified(val uid: String): PhoneAuthState
    data class Error(val message: String): PhoneAuthState
}