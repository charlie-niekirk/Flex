package me.cniekirk.flex.service

import com.google.firebase.messaging.FirebaseMessagingService
import timber.log.Timber

class FlexMessagingService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        Timber.d("TOKEN: $token")
    }
}