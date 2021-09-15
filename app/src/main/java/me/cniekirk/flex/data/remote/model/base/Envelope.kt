package me.cniekirk.flex.data.remote.model.base

interface Envelope<T> {
    val kind: EnvelopeKind?
    val data: T?
}