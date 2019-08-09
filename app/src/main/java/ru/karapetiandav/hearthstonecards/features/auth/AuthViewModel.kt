package ru.karapetiandav.hearthstonecards.features.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.androidhuman.rxfirebase2.auth.rxCreateUserWithEmailAndPassword
import com.androidhuman.rxfirebase2.auth.rxGetCurrentUser
import com.androidhuman.rxfirebase2.auth.rxSignInWithEmailAndPassword
import com.androidhuman.rxfirebase2.auth.rxSignOut
import com.google.firebase.auth.FirebaseAuth
import ru.karapetiandav.hearthstonecards.base.viewmodel.BaseViewModel
import ru.karapetiandav.hearthstonecards.extensions.toUser
import ru.karapetiandav.hearthstonecards.lifecycle.onNext
import ru.karapetiandav.hearthstonecards.providers.rx.SchedulersProvider
import timber.log.Timber

class AuthViewModel(private val authClient: FirebaseAuth, private val schedulers: SchedulersProvider) :
    BaseViewModel() {

    private val _state = MutableLiveData<AuthViewState>()
    val state: LiveData<AuthViewState>
        get() = _state

    fun checkIsUserLogged() {
        authClient.rxGetCurrentUser()
            .subscribeOn(schedulers.io())
            .observeOn(schedulers.mainThread())
            .map<AuthViewState> { AuthLogged(it.toUser()) }
            .subscribe(_state::onNext, { th -> Timber.tag(TAG()).e(th) }, { _state.onNext(AuthNotLogged) })
            .disposeOnViewModelDestroy()
    }

    fun onSignInClick(email: String, password: String) {
        authClient.rxSignInWithEmailAndPassword(email, password)
            .subscribeOn(schedulers.io())
            .observeOn(schedulers.mainThread())
            .map {
                return@map if (it.user != null) {
                    AuthLogged(it.user.toUser())
                } else {
                    AuthNotLogged
                }
            }
            .onErrorReturn(::AuthError)
            .subscribe(_state::onNext) { Timber.tag(TAG()).e(it) }
            .disposeOnViewModelDestroy()
    }

    fun onLogoutClick() {
        authClient.rxSignOut()
            .subscribeOn(schedulers.io())
            .observeOn(schedulers.mainThread())
            .subscribe({ _state.onNext(AuthNotLogged) }, { Timber.tag(TAG()).e(it) })
            .disposeOnViewModelDestroy()
    }

    fun onSignUpClick(email: String, password: String) {
        authClient.rxCreateUserWithEmailAndPassword(email, password)
            .subscribeOn(schedulers.io())
            .observeOn(schedulers.mainThread())
            .subscribe({ _state.onNext(AuthNotLogged) }, { Timber.tag(TAG()).e(it) })
            .disposeOnViewModelDestroy()
    }

}