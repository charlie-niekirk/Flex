package me.cniekirk.flex.data.local.prefs

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import me.cniekirk.flex.FlexSettings
import java.io.InputStream
import java.io.OutputStream

object SettingsSerializer : Serializer<FlexSettings> {

    override val defaultValue: FlexSettings = FlexSettings.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): FlexSettings {
        return try {
            FlexSettings.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: FlexSettings, output: OutputStream) = t.writeTo(output)
}