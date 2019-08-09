package ru.karapetiandav.hearthstonecards.extensions

import android.content.res.Resources
import com.google.firebase.auth.FirebaseUser
import ru.karapetiandav.hearthstonecards.features.auth.User

val Int.toPx: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()

val Int.toDp: Int
    get() = (this / Resources.getSystem().displayMetrics.density).toInt()

fun FirebaseUser.toUser(): User {
    return User(email ?: "", isEmailVerified)
}