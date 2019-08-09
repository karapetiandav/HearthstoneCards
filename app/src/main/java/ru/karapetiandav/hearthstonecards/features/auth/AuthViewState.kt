package ru.karapetiandav.hearthstonecards.features.auth

sealed class AuthViewState
object AuthNotLogged: AuthViewState()
class AuthLogged(val user: User): AuthViewState()
class AuthError(val th: Throwable): AuthViewState()