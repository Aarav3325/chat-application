package com.aarav.chatapplication.presentation.auth

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aarav.chatapplication.ui.theme.manrope
import kotlinx.coroutines.delay

@Preview(showBackground = true)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OPTScreen(
    uiState: AuthUiState,
    viewModel: AuthViewModel,
    navigateToHome: () -> Unit
) {
        var timeLeft by remember { mutableIntStateOf(60) }

        LaunchedEffect(Unit) {
            while (timeLeft > 0) {
                delay(1000L)
                timeLeft--
            }
        }

    LaunchedEffect(Unit) {
        Log.e("OTP", "OTP SCREEN COMPOSED")
    }

    LaunchedEffect(uiState.isVerified) {
        if(uiState.isVerified) {
            navigateToHome()
        }
    }


    var otp by remember { mutableStateOf("") }

        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 72.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {

            Text(
                text = "Enter the 4-digit verification code (OTP) sent to your phone number",
                color = MaterialTheme.colorScheme.onBackground,
                fontFamily = manrope,
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(12.dp))

            Text(
                text = "+91 ${uiState.phone}",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 16.sp,
                fontFamily = manrope,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(24.dp))
            OtpInputField(
                otp = otp,
                onOtpChange = { otp = it }
            )

            Spacer(Modifier.height(54.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                FilledTonalButton(
                    onClick = {
                        // onContinueClick()
                        Log.i("AUTH", "working")
                        uiState.verificationId?.let {

                            viewModel.verifyOtp(it, otp)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    Text(
                        "Continue",
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(Modifier.height(12.dp))

                Text(
                    text = "Expires in $timeLeft seconds",
                    color = Color.Gray,
                    fontSize = 14.sp,
                )

            }
        }
}

@Composable
fun OtpInputField(
    otp: String,
    onOtpChange: (String) -> Unit
) {
    val focusRequesters = remember { List(6) { FocusRequester() } }

    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(6) { index ->
            OutlinedTextField(
                value = otp.getOrNull(index)?.toString() ?: "",
                onValueChange = { value: String ->
                    if (value.isEmpty() || value.first().isDigit()) {
                        val newOtp = otp
                            .padEnd(6, ' ')
                            .toCharArray()
                            .apply { this[index] = value.firstOrNull() ?: ' ' }
                            .concatToString()
                            .trimEnd()

                        onOtpChange(newOtp)

                        if (value.isNotEmpty() && index < 5) {
                            focusRequesters[index + 1].requestFocus()
                        }

                        if(value.isEmpty() && index > 0) {
                            focusRequesters[index - 1].requestFocus()
                        }
                    }
                },
                modifier = Modifier
                    .width(52.dp)
                    .height(52.dp)
                    .focusRequester(focusRequesters[index]),
                textStyle = LocalTextStyle.current.copy(
                    textAlign = TextAlign.Center,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                ),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                shape = RoundedCornerShape(4.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.tertiary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.secondary,
                    cursorColor = MaterialTheme.colorScheme.tertiary
                )
            )
        }
    }
}
