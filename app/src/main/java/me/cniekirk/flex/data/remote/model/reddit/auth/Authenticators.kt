package me.cniekirk.flex.data.remote.model.reddit.auth

import kotlinx.coroutines.runBlocking
import me.cniekirk.flex.data.local.db.dao.PreLoginUserDao
import me.cniekirk.flex.data.local.db.entity.User
import me.cniekirk.flex.data.local.db.dao.UserDao
import me.cniekirk.flex.data.local.db.entity.PreLoginUser
import me.cniekirk.flex.data.remote.RedditApi
import me.cniekirk.flex.util.getHttpBasicAuthHeader
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import java.util.*
import javax.inject.Inject
import javax.inject.Named

const val UNAUTHENTICATED_CODE = 401
const val FORBIDDEN_CODE = 403

class LoggedInAuthenticator @Inject constructor(
    @Named("preAuthApi") private val redditApi: RedditApi,
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

/**
 * Application only OAUTH for pre login endpoints like /api/morechildren for comments
 */
class PreLoginAuthenticator @Inject constructor(
    @Named("preAuthApi") private val redditApi: RedditApi,
    private val preLoginUserDao: PreLoginUserDao
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {

        if (response.code == UNAUTHENTICATED_CODE || response.code == FORBIDDEN_CODE) {

            val user = preLoginUserDao.getAll().firstOrNull()

            val newToken = runBlocking {
                user?.let {
                    redditApi.getUserlessAccessToken(deviceId = user.deviceId)
                } ?: kotlin.run {
                    redditApi.getUserlessAccessToken(deviceId = UUID.randomUUID().toString())
                }
            }

            user?.let { preLoginUserDao.delete(user) }

            preLoginUserDao.insert(PreLoginUser(newToken.accessToken, newToken.deviceId!!))

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