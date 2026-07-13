package com.example.messageapp.domain.model

sealed class SocketState {
    data object Disconnected : SocketState()
    data object Connecting : SocketState()
    data object Connected : SocketState()
    data object Authenticated : SocketState()
    data class Error(val reason: String) : SocketState()
}
