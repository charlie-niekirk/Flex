package me.cniekirk.flex.data.local.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User(
    @PrimaryKey val accessToken: String,
    @ColumnInfo(name = "refresh_token") val refreshToken: String
)