package com.aarav.chatapplication.home

import android.os.Message
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.sp
import com.aarav.chatapplication.R
import com.aarav.chatapplication.ui.theme.manrope

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun HomeScreen(
    navigateToChat: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Messages",
                        fontWeight = FontWeight.Bold,
                        fontSize = 28.sp,
                        fontFamily = manrope,
                        color = Color(0xFF575459)
                    )
                }
            )
        }
    ) {
        LazyColumn(
            modifier = Modifier.padding(it)
        ) {

            item {
                Surface(
                    shape = RoundedCornerShape(28.dp),
                    color = Color(0xFF7C01F6),
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        "Recents",
                        fontFamily = manrope,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }

            items(chatList) { item ->
                ChatItem(
                    item,
                    navigateToChat
                )
            }
        }
    }
}

val chatList = listOf(
    User(
        Color(0xFF009BE4),
        "Rahul",
        "Hey, what's up?",
        "4 min",
        2,
    ),
    User(
        Color(0xFFE8DA5A),
        "Ram",
        "That sounds cool. What...",
        "12 min",
        1,
    ),
    User(
        Color(0xFF00DDC5),
        "Hasti",
        "I like to do a lot of different...",
        "39 min",
        5,
    ),
    User(
        Color(0xFF009BE4),
        "Simran",
        "That's awesome.",
        "45 min",
        4,
    ),
)

data class User(
    val color: Color,
    val name: String,
    val message: String,
    val relativeTime: String,
    val unread: Int,
)

@Preview(showBackground = true)
@Composable
fun ChatItem(
    user: User,
    onClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1F1F1F)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable {
                onClick()
            },
        border = BorderStroke(1.dp, Color(0xFF7D7783))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(67.dp),
                shape = CircleShape,
                color = user.color,
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
                    user.name,
                    fontFamily = manrope,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFFC8C8C8)
                )
                Text(
                    user.message,
                    fontFamily = manrope,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(start = 16.dp)
            ) {
                Text(
                    user.relativeTime,
                    fontFamily = manrope,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF7C01F6)
                )
                Surface(
                    shape = CircleShape,
                    modifier = Modifier.size(22.dp),
                    color = Color(0xFF7C01F6)
                ) {
                    Box(
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            user.unread.toString(),
                            fontFamily = manrope,
                            fontSize = 14.sp,
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }
            }
        }
    }
}