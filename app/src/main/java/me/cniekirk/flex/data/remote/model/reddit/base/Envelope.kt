package me.cniekirk.flex.data.remote.model.reddit.base

interface Envelope<T> {
    val kind: EnvelopeKind?
    val data: T?
}