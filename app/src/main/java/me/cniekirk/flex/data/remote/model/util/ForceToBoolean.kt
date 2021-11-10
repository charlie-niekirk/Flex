package me.cniekirk.flex.data.remote.model.util

import com.squareup.moshi.*

@JsonQualifier
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD, AnnotationTarget.FUNCTION, AnnotationTarget.VALUE_PARAMETER)
annotation class ForceToBoolean

object ForceToBooleanJsonAdapter : JsonAdapter<Boolean>() {

    @ToJson
    override fun toJson(writer: JsonWriter, @ForceToBoolean value: Boolean?) {
        writer.value(value)
    }

    @FromJson
    @ForceToBoolean
    override fun fromJson(reader: JsonReader): Boolean {
        return when (reader.peek()) {
            JsonReader.Token.NUMBER -> reader.nextInt() > 0
            JsonReader.Token.BOOLEAN -> reader.nextBoolean()
            else -> false
        }
    }

}
