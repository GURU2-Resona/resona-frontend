package com.example.resona.data.event

sealed class AuthEvent {
    object Logout : AuthEvent()
}