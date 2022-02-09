package me.cniekirk.flex.data.remote.model.reddit.base

import android.os.Parcelable

interface Thing : Parcelable {
    val id: String?
    val fullname: String?
}