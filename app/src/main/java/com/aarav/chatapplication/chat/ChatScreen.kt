package com.aarav.chatapplication.chat

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aarav.chatapplication.R
import com.aarav.chatapplication.ui.theme.manrope

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun ChatScreen(
    back: () -> Unit
) {

    Scaffold(
        containerColor = Color(0xFF1F1F1F),
    ) {

        Box(
            modifier = Modifier
                .background(
                    Color.Black
                )
                .padding(it)
                .fillMaxSize()
        ) {

            Column(
            ) {
                Box {
                    Row(
                        modifier = Modifier
                            .background(Color(0xFF1F1F1F))
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                modifier = Modifier.size(67.dp),
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
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier
                                    .padding(start = 16.dp)
                                    .weight(1f)
                            ) {
                                Text(
                                    "Rahul",
                                    fontFamily = manrope,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFC8C8C8)
                                )

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Surface(
                                        shape = CircleShape,
                                        color = Color(0xFF00FF85),
                                        modifier = Modifier.size(8.dp)
                                    ) { }

                                    Text(
                                        "Active",
                                        fontFamily = manrope,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF00FF85)
                                    )
                                }
                            }
                        }
                    }
                }


                Box() {

                    LazyColumn(
                        flingBehavior = ScrollableDefaults.flingBehavior(),
                        modifier = Modifier
                            .background(Color.Black)
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

                        items(chats) { chat ->
                            ChatCard(chat)
                        }


                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White,
                        modifier = Modifier.padding(
                            top = 16.dp
                        ).align(Alignment.TopCenter)
                    ) {
                        Text(
                            "Today",
                            fontFamily = manrope,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF4D4D4D),
                            modifier = Modifier.padding(vertical = 6.dp, horizontal = 24.dp)
                        )
                    }
                }
            }
            TextTypeBox(
                Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TextTypeBox(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFF1F1F1F))
    ) {
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(22.dp),
            value = "",
            onValueChange = {

            },
            shape = RoundedCornerShape(24.dp),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                unfocusedContainerColor = Color(0xFF272626),
                focusedContainerColor = Color(0xFF272626)
            ),
            placeholder = {
                Text(
                    "Type here...",
                    fontFamily = manrope,
                    fontSize = 14.sp,
                    color = Color(0xFF8F8F8F)
                )
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
                        tint = Color(0xFF7C01F6),
                        modifier = Modifier.size(24.dp)
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
    chat: Chat
) {
    val bg = if (chat.sent) Color(0xFF830EF7) else Color(0xFF1F1F1F)
    val content = if (chat.sent) Color(0xFFC8C8C8) else Color(0xFFBABABA)
    val alignment = if (chat.sent) Alignment.End else Alignment.Start

    Box(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            horizontalAlignment = alignment,
            modifier = Modifier.fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 16.dp)
        ) {
            Column(
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
                            chat.message,
                            fontFamily = manrope,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.W700,
                            modifier = Modifier,
                            color = content
                        )
                    }
                }
            }
        }
    }
}