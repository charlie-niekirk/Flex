package me.cniekirk.flex.data.remote.model.base

interface Contribution : Thing {
    override val id: String
    override val fullname: String?
}