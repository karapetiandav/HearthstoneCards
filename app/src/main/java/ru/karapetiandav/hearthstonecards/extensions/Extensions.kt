package ru.karapetiandav.hearthstonecards.extensions

import android.content.res.Resources
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.card_details_content.view.*
import ru.karapetiandav.hearthstonecards.features.auth.User

val Int.toPx: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()

val Int.toDp: Int
    get() = (this / Resources.getSystem().displayMetrics.density).toInt()

fun FirebaseUser.toUser(): User {
    return User(email ?: "", isEmailVerified, uid)
}

fun AppCompatActivity.setupBackButton(toolbar: Toolbar, action: () -> Unit) {
    setSupportActionBar(toolbar)
    supportActionBar?.setDisplayHomeAsUpEnabled(true)
    toolbar.setNavigationOnClickListener { action() }
}

fun View.setVisible(isVisible : Boolean) {
    visibility = if (isVisible) {
        VISIBLE
    } else {
        GONE
    }
}

fun FirebaseAuth.isUserLogged(): Boolean = currentUser != null