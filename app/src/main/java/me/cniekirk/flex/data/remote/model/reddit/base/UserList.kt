package me.cniekirk.flex.data.remote.model.reddit.base

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserList<T>(
    val kind: String,
    val data: UserListChild<T>
)

@JsonClass(generateAdapter = true)
data class UserListChild<T>(
    val children: List<T>
)