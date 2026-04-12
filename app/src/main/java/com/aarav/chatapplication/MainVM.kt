package com.aarav.chatapplication

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aarav.chatapplication.data.model.CallModel
import com.aarav.chatapplication.webrtc.SignalingClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainVM
@Inject constructor(
    val signalingClient: SignalingClient
) : ViewModel() {
    private val _incomingCall = MutableSharedFlow<CallModel>()
    val incomingCall = _incomingCall.asSharedFlow()

    fun listenForIncomingCalls(userId: String) {
        Log.d("CALL", "Listening for incoming calls")

        viewModelScope.launch {
            signalingClient.listenForIncomingCalls(userId)
                .collect { call ->
                    if (call.offer != null && call.answer == null) {
                        _incomingCall.emit(call)
                    }
                }
        }
    }

}