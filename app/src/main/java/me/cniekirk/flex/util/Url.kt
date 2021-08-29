package me.cniekirk.flex.util

import android.util.Base64.NO_WRAP
import android.util.Base64.encodeToString
import java.util.*

const val BASE_URL = "https://m.reddit.com/api/v1/authorize.compact"
const val CLIENT_ID = "14spBPt-yu7A6NJ7tdByhg"
const val USER_AGENT = "android:me.cniekirk.flexforreddit:v1.0 (by /u/chertycherty)"

private const val RANDSTR_MIN = 1
private const val RANDSTR_MAX = 32

private val STRING_CHARACTERS = ('0'..'9').plus('a'..'z').toTypedArray()

fun provideAuthorizeUrl(): String {

    val params = arrayOf(
        "client_id=$CLIENT_ID",
        "response_type=code",
        "state=${generateRandomString()}",
        "redirect_uri=https://localhost",
        "duration=permanent",
        "scope=identity edit flair history modconfig modflair modlog modposts modwiki mysubreddits privatemessages read report save submit subscribe vote wikiedit wikiread creddits modcontributors modmail modothers livemanage account modself"
    )

    return addParamsToUrl(BASE_URL, params)
}

fun String.isRedirectUri() = this.startsWith("https://localhost")

fun addParamsToUrl(url: String, array: Array<String>): String {
    return url.plus(array.joinToString(separator = "&", prefix = "?"))
}

fun generateRandomString(): String {
    return (RANDSTR_MIN..RANDSTR_MAX).map { STRING_CHARACTERS.random() }.joinToString("")
}

fun String.toAuthParams(): Map<String, String> {
    val params: MutableMap<String, String> = HashMap()
    params["grant_type"] = "authorization_code"
    params["code"] = this
    params["redirect_uri"] = "https://localhost"
    return params
}

fun getHttpBasicAuthHeader(): Map<String, String> {
    val params: MutableMap<String, String> = HashMap()
    val credentials = java.lang.String.format("%s:%s", CLIENT_ID, "")
    val auth = "Basic " + encodeToString(credentials.toByteArray(), NO_WRAP)
    params["Authorization"] = auth
    params["User-Agent"] = USER_AGENT
    return params
}

fun getDeviceUUID(): String {
    return UUID.randomUUID().toString()
}