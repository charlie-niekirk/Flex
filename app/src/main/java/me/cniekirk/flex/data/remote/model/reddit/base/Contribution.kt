package me.cniekirk.flex.data.remote.model.reddit.base

interface Contribution : Thing {
    override val id: String?
    override val fullname: String?
}