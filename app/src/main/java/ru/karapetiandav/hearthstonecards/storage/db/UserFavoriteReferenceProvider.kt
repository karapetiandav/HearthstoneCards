package ru.karapetiandav.hearthstonecards.storage.db

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import ru.karapetiandav.hearthstonecards.features.auth.User

/*
*  Example: How to write paths
*  firebase.database().ref('info/important/personal').set(true);
* */
class UserFavoriteReferenceProvider(
    private val currentUser: User?,
    private val firebaseDatabase: FirebaseDatabase
) : ReferenceProvider {
    override fun getReference(): DatabaseReference? {
        return if (currentUser != null) {
            firebaseDatabase.getReference("users/${currentUser.uid}/favorites")
        } else {
            null
        }
    }
}