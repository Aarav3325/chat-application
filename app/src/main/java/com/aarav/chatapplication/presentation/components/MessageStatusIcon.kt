package com.aarav.chatapplication.presentation.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.aarav.chatapplication.R
import com.aarav.chatapplication.data.model.Message
import com.aarav.chatapplication.data.model.MessageStatus

@Composable
fun MessageStatusIcon(status: String) {
    when(status) {
        MessageStatus.SENT.name -> {
            Icon(
                painter = painterResource(R.drawable.sent),
                contentDescription = "sent",
                modifier = Modifier.size(24.dp),
                tint = Color.Gray
            )
        }

        MessageStatus.DELIVERED.name -> {
            Icon(
                painter = painterResource(R.drawable.delivered),
                modifier = Modifier.size(24.dp),
                contentDescription = "delivered",
                tint = Color.Gray
            )
        }

        MessageStatus.READ.name -> {
            Icon(
                painter = painterResource(R.drawable.delivered),
                modifier = Modifier.size(24.dp),
                contentDescription = "read",
                tint = MaterialTheme.colorScheme.tertiary
            )
        }
    }
}