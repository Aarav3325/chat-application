package com.aarav.chatapplication.presentation.call

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.aarav.chatapplication.R
import com.aarav.chatapplication.utils.formatTime
import kotlinx.coroutines.delay
import org.webrtc.SurfaceViewRenderer

@Composable
fun OneToOneCallScreen(
    callId: String,
    myUserId: String,
    callerName: String,
    isCaller: Boolean,
    isVideoCall: Boolean,
    onCallEnd: () -> Unit,
    viewModel: CallViewModel
) {
    val context = LocalContext.current

    val localView = remember {
        SurfaceViewRenderer(context)
    }

    val remoteView = remember {
        SurfaceViewRenderer(context)
    }


    val eglBaseContext by viewModel.eglContext.collectAsState()

    val state by viewModel.callState.collectAsState()

    val tracks by viewModel.tracks.collectAsState()

    val callEnded by viewModel.callEnded.collectAsState()
    val isMuted by viewModel.isMuted.collectAsState()

    var isSpeakerOn by remember { mutableStateOf(true) }

    val time by viewModel.callTime.collectAsState()


    val isVideoEnabled by viewModel.isVideoEnabled.collectAsState()


    val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    val focusRequest = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_VOICE_COMMUNICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build()
            )
            .setAcceptsDelayedFocusGain(false)
            .setOnAudioFocusChangeListener {}
            .build()
    } else null

    LaunchedEffect(callId) {
        audioManager.mode = AudioManager.MODE_IN_COMMUNICATION
        audioManager.isSpeakerphoneOn = isSpeakerOn
        audioManager.isMicrophoneMute = false

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            audioManager.requestAudioFocus(focusRequest!!)
        } else {
            audioManager.requestAudioFocus(
                null,
                AudioManager.STREAM_VOICE_CALL,
                AudioManager.AUDIOFOCUS_GAIN
            )
        }

        if (!isCaller) {
            viewModel.receiveCall(callId, myUserId, isVideoCall)
        }
    }

    LaunchedEffect(state) {
        if (state == "CONNECTED") {
            viewModel.refreshAudio()
        }
    }

    LaunchedEffect(isSpeakerOn) {
        audioManager.isSpeakerphoneOn = isSpeakerOn
    }

    LaunchedEffect(state) {
        if (state == "BUSY") {
            try {
                val toneGen = ToneGenerator(AudioManager.STREAM_VOICE_CALL, 100)
                toneGen.startTone(ToneGenerator.TONE_SUP_BUSY, 2000)
                kotlinx.coroutines.delay(2000)
                toneGen.release()
            } catch (e: Exception) {
                Log.e("CALL", "Tone fail", e)
            }
        }
    }


    LaunchedEffect(Unit) {

        viewModel.events.collect { event ->
            if (event is UiEvent.EndCall) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    audioManager.abandonAudioFocusRequest(focusRequest!!)
                } else {
                    audioManager.abandonAudioFocus(null)
                }

                try {
                    localView.clearImage()
                    remoteView.clearImage()
                } catch (_: Exception) {
                }

                audioManager.mode = AudioManager.MODE_NORMAL
                audioManager.isSpeakerphoneOn = false
                audioManager.isMicrophoneMute = false

                delay(1500)

                onCallEnd()
            }
        }
    }


    LaunchedEffect(tracks) {
        if (isVideoCall && !callEnded) {

            val localTrack = tracks["LOCAL"]
            localTrack?.let {
                try {
                    it.addSink(localView)
                } catch (_: Exception) {
                }
            }

            val remoteTrack = tracks.entries
                .firstOrNull { it.key != "LOCAL" }
                ?.value

            remoteTrack?.let {
                try {
                    it.addSink(remoteView)
                } catch (_: Exception) {
                }
            }
        }
    }

    LaunchedEffect(eglBaseContext) {
        if (eglBaseContext != null) {

            remoteView.init(eglBaseContext, null)
            remoteView.setMirror(false)
            remoteView.setEnableHardwareScaler(true)
            remoteView.setZOrderOnTop(false)
            remoteView.setZOrderMediaOverlay(false)

            localView.init(eglBaseContext, null)
            localView.setMirror(true)
            localView.setEnableHardwareScaler(true)
            localView.setZOrderMediaOverlay(true)
        }
    }


    DisposableEffect(Unit) {


        onDispose {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                audioManager.abandonAudioFocusRequest(focusRequest!!)
            } else {
                audioManager.abandonAudioFocus(null)
            }

            audioManager.mode = AudioManager.MODE_NORMAL
            audioManager.isSpeakerphoneOn = false
            audioManager.isMicrophoneMute = false

//            val localTrack = tracks["LOCAL"]
//            localTrack?.removeSink(localView)
//
//            val remoteTrack = tracks.entries
//                .firstOrNull { it.key != "LOCAL" }
//                ?.value
//
//            remoteTrack?.removeSink(remoteView)

            try {
                localView.clearImage()
                localView.release()
            } catch (_: Exception) {
            }

            try {
                remoteView.clearImage()
                remoteView.release()
            } catch (_: Exception) {
            }

        }
    }


    Scaffold(
        containerColor = Color.Transparent,
        modifier = Modifier.fillMaxSize()
    ) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {

            if (isVideoCall) {

                AndroidView(
                    factory = { remoteView },
                    modifier = Modifier
                        .fillMaxSize()
                )

                if (isVideoEnabled) {
                    AndroidView(
                        factory = { localView },
                        modifier = Modifier
                            .padding(top = 78.dp, end = 16.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .size(120.dp, 160.dp)
                            .align(Alignment.TopEnd)
                            .background(Color.Black)
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .padding(top = 78.dp, end = 16.dp)
                            .size(120.dp, 160.dp)
                            .align(Alignment.TopEnd)
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.surfaceContainerLow),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                painter = painterResource(R.drawable.camera_off),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "You",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surface),
                    contentAlignment = Alignment.Center
                ) {
                    Column() {
                        Text(callerName, color = MaterialTheme.colorScheme.onSurface, fontSize = 22.sp)

                        if (!callEnded && state == "CONNECTED") {
                            Spacer(Modifier.height(6.dp))

                            Text(
                                text = formatTime(time),
                                color = Color.White,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .background(Color.Transparent)
                    .padding(horizontal = 12.dp, vertical = 12.dp),
            ) {

                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = when (state) {
                            "CALLING" -> "Calling $callerName..."
                            "RECEIVING" -> "Incoming call from $callerName..."
                            "CONNECTING" -> "Connecting to $callerName..."
                            "CONNECTED" -> callerName
                            "DISCONNECTED" -> "Disconnected"
                            "FAILED" -> "Failed"
                            "CLOSED", "ENDED" -> "Call Ended"
                            "BUSY" -> "$callerName is Busy"
                            "REJECTED" -> "Call Declined"
                            "MISSED" -> "Missed Call"
                            "IDLE" -> if (callEnded) "Call Ended" else "Initializing..."
                            else -> "Initializing..."
                        },
                        color = Color.White,
                        style = MaterialTheme.typography.bodyLarge
                    )

                    if (!callEnded && state == "CONNECTED" && isVideoCall) {
                        Text(
                            text = formatTime(time),
                            color = Color.White,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            CallActionToolbar(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 54.dp),
                isMicEnabled = !isMuted,
                isCaller = isCaller,
                isGroupCall = false,
                isSpeakerOn = isSpeakerOn,
                isVideoEnabled = isVideoEnabled,
                isVideoCall = isVideoCall,
                onMicClick = { viewModel.toggleMute() },
                onSpeakerClick = {
                    isSpeakerOn = !isSpeakerOn
                    audioManager.isSpeakerphoneOn = isSpeakerOn
                },
                onEndCallClick = {
                    viewModel.endCall(callId)
                },
                leaveCall = {},
                toggleVideo = {
                    viewModel.toggleVideo()
                },
                toggleCamera = {
                    viewModel.toggleCamera()
                }
            )

        }

    }


}