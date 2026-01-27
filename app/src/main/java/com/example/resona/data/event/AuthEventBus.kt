package com.example.resona.data.event

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthEventBus @Inject constructor() {

    private val _event = MutableSharedFlow<AuthEvent>(
        replay = 0,
        extraBufferCapacity = 1
    )
    val event = _event.asSharedFlow()

    @Volatile
    private var isLoggedOut = false

    fun emitLogoutOnce() {
        if (isLoggedOut) return
        isLoggedOut = true
        _event.tryEmit(AuthEvent.Logout)
    }

    fun isLoggedOut(): Boolean = isLoggedOut

    fun reset() {
        isLoggedOut = false
    }
}
