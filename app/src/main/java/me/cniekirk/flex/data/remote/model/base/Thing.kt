package me.cniekirk.flex.data.remote.model.base

import android.os.Parcelable

interface Thing : Parcelable {
    val id: String
    val fullname: String?
}