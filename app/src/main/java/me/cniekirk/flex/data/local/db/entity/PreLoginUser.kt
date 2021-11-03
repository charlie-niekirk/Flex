package me.cniekirk.flex.data.local.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PreLoginUser(
    @PrimaryKey val accessToken: String,
    @ColumnInfo(name = "device_id") val deviceId: String
)