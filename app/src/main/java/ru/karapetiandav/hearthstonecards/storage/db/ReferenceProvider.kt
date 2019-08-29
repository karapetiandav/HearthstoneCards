package ru.karapetiandav.hearthstonecards.storage.db

import com.google.firebase.database.DatabaseReference

interface ReferenceProvider {
    fun getReference(): DatabaseReference?
}