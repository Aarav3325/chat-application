package com.aarav.chatapplication.home

import android.util.Log
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aarav.chatapplication.R
import com.aarav.chatapplication.components.CreateChatModalSheet
import com.aarav.chatapplication.components.CustomBottomSheet
import com.aarav.chatapplication.ui.theme.manrope

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun HomeScreen(
    navigateToChat: () -> Unit,
    charViewModel: ChatViewModel,
) {

    val userList by charViewModel.userList.collectAsState()

    var showCreateChatModal by remember {
        mutableStateOf(false)
    }

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false
    )

    if(showCreateChatModal) {
        CustomBottomSheet(
            sheetState = sheetState,
            onDismiss = {
                showCreateChatModal = false
            },
            title = "Create New Chat"
        ) {
            CreateChatModalSheet(userList)
        }
    }

    LaunchedEffect(userList) {
        if(userList.isNotEmpty()) {
            Log.i("MYTAG", userList.toString())
        }
    }

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
                },
                actions = {
                    IconButton(
                        onClick = {
                            showCreateChatModal = true
                        }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.create_chat),
                            contentDescription = "create chat",
                            tint = MaterialTheme.colorScheme.secondary
                        )
                    }
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
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        "Recents",
                        fontFamily = manrope,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
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
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable {
                onClick()
            },
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
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
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    user.message,
                    fontFamily = manrope,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
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
                    color = MaterialTheme.colorScheme.primary
                )
                Surface(
                    shape = CircleShape,
                    modifier = Modifier.size(22.dp),
                    color = MaterialTheme.colorScheme.primary
                ) {
                    Box(
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            user.unread.toString(),
                            fontFamily = manrope,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }
            }
        }
    }
}