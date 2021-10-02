package me.cniekirk.flex.data.remote.auth

import kotlinx.coroutines.runBlocking
import me.cniekirk.flex.data.local.db.User
import me.cniekirk.flex.data.local.db.UserDao
import me.cniekirk.flex.data.remote.RedditApi
import me.cniekirk.flex.util.getHttpBasicAuthHeader
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject
import javax.inject.Named

const val UNAUTHENTICATED_CODE = 401

class AccessTokenAuthenticator @Inject constructor(
    @Named("userlessApi") private val redditApi: RedditApi,
    private val userDao: UserDao
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {

        if (response.code == UNAUTHENTICATED_CODE) {

            val user = userDao.getAll().first()

            val newToken = runBlocking {
                redditApi.renewToken(getHttpBasicAuthHeader(), refreshToken = user.refreshToken)
            }

            userDao.delete(user)
            userDao.insert(User(newToken.accessToken, newToken.refreshToken!!))

            return response
                .request
                .newBuilder()
                .header("Authorization", "Bearer ${newToken.accessToken}")
                .build()

        } else {
            return null
        }

    }

}