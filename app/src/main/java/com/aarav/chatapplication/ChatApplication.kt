package com.aarav.chatapplication

import android.app.Application
import androidx.compose.ui.res.stringResource
import com.posthog.android.PostHogAndroid
import com.posthog.android.PostHogAndroidConfig
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ChatApplication: Application() {
    companion object {
        const val POSTHOG_HOST = "https://us.i.posthog.com"
    }

    override fun onCreate() {
        super.onCreate()


        val config = PostHogAndroidConfig(
            apiKey = applicationContext.getString(R.string.posthog_api),
            host = POSTHOG_HOST
        )
        PostHogAndroid.setup(this, config)
    }
}