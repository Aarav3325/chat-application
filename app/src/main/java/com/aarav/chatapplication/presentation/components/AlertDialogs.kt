package com.aarav.chatapplication.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.aarav.chatapplication.R
import com.aarav.chatapplication.ui.theme.manrope

@Composable
fun MyAlertDialog(
    modifier: Modifier = Modifier,
    shouldShowDialog: Boolean,
    onDismissRequest: () -> Unit,
    title: String,
    message: String,
    confirmButtonText: String,
    onConfirmClick: () -> Unit,
) {
    if (shouldShowDialog) {
        AlertDialog(
            modifier = modifier,
            onDismissRequest = onDismissRequest,
            confirmButton = {
                FilledTonalButton(onClick = onConfirmClick,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )) {
                    Text(confirmButtonText, fontFamily = manrope)
                }
            },
            containerColor = MaterialTheme.colorScheme.background,
            title = {
                Text(
                    text = title,
                    fontFamily = manrope,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            },
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.bug_droid),
                    contentDescription = "Error icon",
                    tint = MaterialTheme.colorScheme.error
                )
            },
            text = {
                Text(
                    text = message,
                    fontFamily = manrope,
                    textAlign = TextAlign.Center,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        )
    }
}