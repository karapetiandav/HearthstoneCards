package ru.karapetiandav.hearthstonecards.features.auth


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jakewharton.rxbinding3.view.focusChanges
import com.jakewharton.rxbinding3.widget.textChanges
import com.redmadrobot.lib.sd.base.State
import com.redmadrobot.lib.sd.base.StateDelegate
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_auth.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.karapetiandav.hearthstonecards.R
import ru.karapetiandav.hearthstonecards.base.fragment.BaseFragment
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
            State(AuthScreenState.LOGGED, listOf(user_email, logout_button))
        )

        observe(viewModel.state, ::onStateChanged)

        viewModel.checkIsUserLogged()
    }

    private fun onStateChanged(state: AuthViewState) {
        when (state) {
            is AuthNotLogged -> {
                screenState.currentState = AuthScreenState.NOT_LOGGED

                sign_in_button.setOnClickListener {
                    viewModel.onSignInClick(email_edittext.text.toString(), password_edittext.text.toString())
                }

                sign_up_button.setOnClickListener {
                    viewModel.onSignUpClick(email_edittext.text.toString(), password_edittext.text.toString())
                }
            }
            is AuthLogged -> {
                screenState.currentState = AuthScreenState.LOGGED
                user_email.text = state.user.email
                logout_button.setOnClickListener { viewModel.onLogoutClick() }
            }
            is AuthError -> {
                screenState.currentState = AuthScreenState.ERROR
            }
        }
    }
}
