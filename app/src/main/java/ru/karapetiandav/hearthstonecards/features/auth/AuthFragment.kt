package ru.karapetiandav.hearthstonecards.features.auth


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.jakewharton.rxbinding3.view.clicks
import com.jakewharton.rxbinding3.widget.textChanges
import com.redmadrobot.lib.sd.base.State
import com.redmadrobot.lib.sd.base.StateDelegate
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import kotlinx.android.synthetic.main.fragment_auth.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.karapetiandav.hearthstonecards.R
import ru.karapetiandav.hearthstonecards.base.fragment.BaseFragment
import ru.karapetiandav.hearthstonecards.features.auth.validation.AuthErrorHandler
import ru.karapetiandav.hearthstonecards.lifecycle.observe
import timber.log.Timber
import java.util.concurrent.TimeUnit

class AuthFragment : BaseFragment() {

    private lateinit var screenState: StateDelegate<AuthScreenState>

    private val viewModel: AuthViewModel by viewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_auth, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        screenState = StateDelegate(
            State(
                AuthScreenState.NOT_LOGGED,
                listOf(email_text_layout, password_text_layout, sign_in_button, sign_up_button)
            ),
            State(
                AuthScreenState.LOGGED,
                listOf(user_email, logout_button)
            )
        )

        observe(viewModel.state, ::onStateChanged)

        viewModel.checkIsUserLogged()
    }

    private fun onStateChanged(state: AuthViewState) {
        when (state) {
            is AuthNotLogged -> {
                screenState.currentState = AuthScreenState.NOT_LOGGED

                val emailObservable = email_edittext.textChanges()
                val passwordObservable = password_edittext.textChanges()

                emailObservable
                    .skipInitialValue()
                    .distinctUntilChanged()
                    .doOnNext { hideEmailError() }
                    .debounce(300, TimeUnit.MILLISECONDS)
                    .map { it.toString() }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe()
                    .disposeOnViewDestroy()

                passwordObservable
                    .skipInitialValue()
                    .distinctUntilChanged()
                    .doOnNext { hidePasswordError() }
                    .debounce(300, TimeUnit.MILLISECONDS)
                    .map { it.toString() }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe()
                    .disposeOnViewDestroy()

                Observable.combineLatest<CharSequence, CharSequence, AuthErrorHandler>(
                    emailObservable,
                    passwordObservable, BiFunction { email, pass ->
                        val emailValidationResult = viewModel.validateEmail(email.toString())
                        val passValidationResult = viewModel.validatePassword(pass.toString())

                        return@BiFunction AuthErrorHandler(
                            emailValidationResult,
                            passValidationResult
                        )
                    })
                    .skip(1) // For not showing errors at start
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(::showErrors) { Timber.e(it) }

                sign_in_button.clicks()
                    .throttleFirst(1000, TimeUnit.MILLISECONDS)
                    .map { Credentials(email_edittext.text.toString(), password_edittext.text.toString()) }
                    .subscribe(viewModel::onSignInClick) { Timber.e(it) }

                sign_up_button.clicks()
                    .throttleFirst(1000, TimeUnit.MILLISECONDS)
                    .map { Credentials(email_edittext.text.toString(), password_edittext.text.toString()) }
                    .subscribe(viewModel::onSignUpClick) { Timber.e(it) }
            }
            is AuthLogged -> {
                screenState.currentState = AuthScreenState.LOGGED
                user_email.text = state.user.email
                logout_button.clicks()
                    .throttleFirst(1000, TimeUnit.MILLISECONDS)
                    .subscribe({ viewModel.onLogoutClick() }, { Timber.e(it) })
            }
            is AuthError -> {
                Toast.makeText(context, state.th.localizedMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun hidePasswordError() {
        password_text_layout.error = null
    }

    private fun hideEmailError() {
        email_text_layout.error = null
    }

    private fun showErrors(errors: AuthErrorHandler) {
        email_text_layout.error = errors.emailError
        password_text_layout.error = errors.passwordError
        val isValid = errors.emailError == null && errors.passwordError == null
        sign_in_button.isEnabled = isValid
        sign_up_button.isEnabled = isValid
    }
}