package ru.karapetiandav.hearthstonecards.features.auth

import androidx.core.util.PatternsCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.androidhuman.rxfirebase2.auth.rxCreateUserWithEmailAndPassword
import com.androidhuman.rxfirebase2.auth.rxGetCurrentUser
import com.androidhuman.rxfirebase2.auth.rxSignInWithEmailAndPassword
import com.androidhuman.rxfirebase2.auth.rxSignOut
import com.google.firebase.auth.FirebaseAuth
import ru.karapetiandav.hearthstonecards.CardsScreen
import ru.karapetiandav.hearthstonecards.base.viewmodel.BaseViewModel
import ru.karapetiandav.hearthstonecards.extensions.toUser
import ru.karapetiandav.hearthstonecards.lifecycle.onNext
import ru.karapetiandav.hearthstonecards.providers.rx.SchedulersProvider
import ru.terrakok.cicerone.Router
import timber.log.Timber

class AuthViewModel(
    private val authClient: FirebaseAuth,
    private val schedulers: SchedulersProvider,
    private val router: Router
) : BaseViewModel() {

    private val _state = MutableLiveData<AuthViewState>()
    val state: LiveData<AuthViewState>
        get() = _state

    fun checkIsUserLogged() {
        authClient.rxGetCurrentUser()
            .map<AuthViewState> { AuthLogged(it.toUser()) }
            .subscribeOn(schedulers.io())
            .observeOn(schedulers.mainThread())
            .subscribe(_state::onNext, { th -> Timber.e(th) }, { _state.onNext(AuthNotLogged) })
            .disposeOnViewModelDestroy()
    }

    fun onSignInClick(credentials: Credentials) {
        authClient.rxSignInWithEmailAndPassword(credentials.email, credentials.password)
            .map {
                return@map if (it.user != null) {
                    AuthLogged(it.user.toUser())
                } else {
                    AuthNotLogged
                }
            }
            .onErrorReturn(::AuthError)
            .subscribeOn(schedulers.io())
            .observeOn(schedulers.mainThread())
            .subscribe(_state::onNext) { Timber.e(it) }
            .disposeOnViewModelDestroy()
    }

    fun onLogoutClick() {
        authClient.rxSignOut()
            .subscribeOn(schedulers.io())
            .observeOn(schedulers.mainThread())
            .subscribe({ _state.onNext(AuthNotLogged) }, { Timber.e(it) })
            .disposeOnViewModelDestroy()
    }

    fun onSignUpClick(credentials: Credentials) {
        authClient.rxCreateUserWithEmailAndPassword(credentials.email, credentials.password)
            .map<AuthViewState> { AuthLogged(it.toUser()) }
            .onErrorReturn(::AuthError)
            .subscribeOn(schedulers.io())
            .observeOn(schedulers.mainThread())
            .subscribe(_state::onNext) { Timber.e(it) }
            .disposeOnViewModelDestroy()
    }

    fun validateEmail(email: String): String? {
        return if (PatternsCompat.EMAIL_ADDRESS.matcher(email).matches()) {
            null
        } else {
            "Enter correct e-mail"
        }
    }

    fun validatePassword(pass: String): String? {
        return if (pass.length > 6) {
            null
        } else {
            "Password must be at least 6 symbols"
        }
    }

    fun onBackPressed() {
        router.backTo(CardsScreen)
    }
}