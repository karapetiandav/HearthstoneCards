package ru.karapetiandav.hearthstonecards.extensions

import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseUser
import ru.karapetiandav.hearthstonecards.features.auth.User

val Int.toPx: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()

val Int.toDp: Int
    get() = (this / Resources.getSystem().displayMetrics.density).toInt()

fun FirebaseUser.toUser(): User {
    return User(email ?: "", isEmailVerified)
}

fun AppCompatActivity.setupBackButton(toolbar: Toolbar, action: () -> Unit) {
    setSupportActionBar(toolbar)
    supportActionBar?.setDisplayHomeAsUpEnabled(true)
    toolbar.setNavigationOnClickListener { action() }
}