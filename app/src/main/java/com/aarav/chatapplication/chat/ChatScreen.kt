package com.aarav.chatapplication.chat

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aarav.chatapplication.R
import com.aarav.chatapplication.components.MessageStatusIcon
import com.aarav.chatapplication.components.MyAlertDialog
import com.aarav.chatapplication.data.model.Message
import com.aarav.chatapplication.home.ChatViewModel
import com.aarav.chatapplication.ui.theme.manrope
import java.text.SimpleDateFormat
import java.util.Date

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun ChatScreen(
    chatId: String,
    myId: String,
    otherUserId: String,
    back: () -> Unit,
    chatViewModel: ChatViewModel
) {

    val uiState by chatViewModel.uiState.collectAsState()

    val context = LocalContext.current

    var text by remember {
        mutableStateOf("")
    }

    LaunchedEffect(Unit) {
        chatViewModel.observeMessages(chatId, myId)
        chatViewModel.observePresence(otherUserId)
    }


    MyAlertDialog(
        shouldShowDialog = uiState.showErrorDialog,
        onDismissRequest = {
            chatViewModel.clearError()
        },
        title = "Error",
        message = uiState.error ?: "Something went wrong",
        confirmButtonText = "Dismiss",
    ) {
        chatViewModel.clearError()
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0),
        containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
    ) {

        Box(
            modifier = Modifier
                .background(
                    MaterialTheme.colorScheme.surfaceContainerLowest
                )
                .padding(it)
                .fillMaxSize()
        ) {

            Column(
            ) {
                Box {
                    Row(
                        modifier = Modifier
                            .padding(top = 48.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier.clip(CircleShape).clickable {
                                    back()
                                    chatViewModel.onTypingStopped()
                                }
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.arrow_back),
                                    contentDescription = "back",
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .size(18.dp)
                                )
                            }

                            Spacer(Modifier.width(8.dp))

                            Surface(
                                modifier = Modifier.size(48.dp),
                                shape = CircleShape,
                                color = Color(0xFF00DDC5),
                            ) {
                                Image(
                                    painter = painterResource(R.drawable.user),
                                    contentDescription = "avatar",
                                    modifier = Modifier
                                        .size(36.dp)
                                        .padding(8.dp)
                                )
                            }

                            Column(
                                verticalArrangement = Arrangement.spacedBy(0.dp),
                                modifier = Modifier
                                    .padding(start = 16.dp)
                                    .weight(1f)
                            ) {
                                Text(
                                    "Rahul",
                                    fontFamily = manrope,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    if (uiState.isOtherUserTyping) {
                                        Text(
                                            "typing...",
                                            fontFamily = manrope,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.secondary
                                        )
                                    } else {
                                        when {
                                            uiState.presence == null -> ""
                                            uiState.presence!!.isOnline -> {
                                                Surface(
                                                    shape = CircleShape,
                                                    color = Color(0xFF00FF85),
                                                    modifier = Modifier.size(8.dp)
                                                ) { }

                                                Text(
                                                    "Online",
                                                    fontFamily = manrope,
                                                    fontSize = 14.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFF00FF85)
                                                )
                                            }

                                            uiState.presence!!.lastSeen > 0 -> {
                                                Text(
                                                    "last active at ${formatTimestamp(uiState.presence!!.lastSeen)}",
                                                    fontFamily = manrope,
                                                    fontSize = 14.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.tertiary
                                                )
                                            }

                                        }
                                    }
                                }
                            }
                        }
                    }
                }


                Box() {

                    LazyColumn(
                        flingBehavior = ScrollableDefaults.flingBehavior(),
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.surfaceContainerLow)
                            .fillMaxSize()
                            .padding(bottom = 102.dp)
                    ) {
//                        stickyHeader {
//
//                            Box(
//                                contentAlignment = Alignment.Center,
//                                modifier = Modifier.fillMaxWidth()
//                            ) {
//
//                            }
//
//                        }

                        items(uiState.messages) { chat ->

                            val isMine = chat.senderId == myId

                            ChatCard(chat, isMine)
                        }


                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier
                            .padding(
                                top = 16.dp
                            )
                            .align(Alignment.TopCenter)
                    ) {
                        Text(
                            "Today",
                            fontFamily = manrope,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onTertiary,
                            modifier = Modifier.padding(vertical = 6.dp, horizontal = 24.dp)
                        )
                    }
                }
            }
            TextTypeBox(
                text,
                onValueChange = {
                    text = it
                },
                onStartTyping = {
                    chatViewModel.onTypingStarted()
                },
                onStopTyping = {
                    chatViewModel.onTypingStopped()
                },
                error = uiState.messageError,
                Modifier.align(Alignment.BottomCenter)
            ) {
                chatViewModel.sendMessages(otherUserId, text)
                text = ""
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TextTypeBox(
    text: String,
    onValueChange: (String) -> Unit,
    onStartTyping: () -> Unit,
    onStopTyping: () -> Unit,
    error: String?,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
    ) {
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(22.dp),
            value = text,
            onValueChange = {
                onValueChange(it)
                if(it.isNotBlank()) {
                    onStartTyping()
                }
                else {
                    onStopTyping()
                }
            },
            shape = RoundedCornerShape(24.dp),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                focusedContainerColor = MaterialTheme.colorScheme.surfaceContainer
            ),
            placeholder = {
                if (error == null) {
                    Text(
                        "Type here...",
                        fontFamily = manrope,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.secondary
                    )
                } else {
                    Text(
                        error,
                        fontFamily = manrope,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            },
            trailingIcon = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(end = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    VerticalDivider(
                        modifier = Modifier
                            .width(2.dp)
                            .height(28.dp)
                    )

                    Icon(
                        painter = painterResource(R.drawable.send),
                        contentDescription = "sticker",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .size(24.dp)
                            .clickable {
                                onClick()
                            }
                    )
                }
            }
        )
    }
}


data class Chat(
    val message: String,
    val sent: Boolean
)

val chats = listOf(
    Chat(
        "Hey, what's up?",
        false
    ),
    Chat(
        "Not much, just hanging out at home. How about you?",
        true
    ),
    Chat(
        "Same here. I've been trying to stay busy by working on some art projects.",
        false
    ),
    Chat(
        "That sounds cool. What kind of art are you into?",
        true
    ),

    Chat(
        "I like to do a lot of different things, but right now I'm really into painting. I've been working on a series of abstract landscapes.",
        false
    ),

    Chat(
        "That sounds cool. What kind of art are you into?",
        true
    ),

    Chat(
        "I like to do a lot of different things, but right now I'm really into painting. I've been working on a series of abstract landscapes.",
        false
    ),

    Chat(
        "That sounds cool. What kind of art are you into?",
        true
    ),

    Chat(
        "I like to do a lot of different things, but right now I'm really into painting. I've been working on a series of abstract landscapes.",
        false
    ),
)

@Preview(showBackground = true)
@Composable
fun ChatCard(
    message: Message,
    isMine: Boolean
) {
    val bg =
        if (isMine) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.secondaryContainer
    val content =
        if (isMine) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSecondaryContainer
    val alignment = if (isMine) Alignment.End else Alignment.Start

    Box(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            horizontalAlignment = alignment,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 16.dp)
        ) {
            Column(
                horizontalAlignment = alignment,
                modifier = Modifier.fillMaxWidth(0.85f)
            ) {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = bg
                    ),
                    modifier = Modifier,
                ) {

                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                    ) {
                        Text(
                            message.text,
                            fontFamily = manrope,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.W700,
                            modifier = Modifier,
                            color = content
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            Row() {
                Text(
                    formatTimestamp(message.timestamp),
                    fontFamily = manrope,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.W700,
                    modifier = Modifier,
                    color = content
                )

                Spacer(Modifier.width(4.dp))

                Surface(
                    color = Color.Transparent,
                    modifier = Modifier
                ) {
                    if (isMine) {
                        MessageStatusIcon(message.status)
                    }
                }
            }
        }
    }
}

fun formatTimestamp(timestamp: Long): String {
    val df = SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
    return df.format(Date(timestamp))
}