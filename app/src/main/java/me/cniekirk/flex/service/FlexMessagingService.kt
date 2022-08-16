package me.cniekirk.flex.service

import androidx.datastore.core.DataStore
import com.google.firebase.messaging.FirebaseMessagingService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import me.cniekirk.flex.FlexSettings
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class FlexMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var flexSettings: DataStore<FlexSettings>

    private val job = SupervisorJob()

    override fun onNewToken(token: String) {
        Timber.d("TOKEN: $token")
        CoroutineScope(Dispatchers.IO + job).launch {
            flexSettings.updateData { settings ->
                settings.toBuilder()
                    .setDeviceToken(token)
                    .build()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
}