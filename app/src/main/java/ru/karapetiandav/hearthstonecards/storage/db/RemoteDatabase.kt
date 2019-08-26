package ru.karapetiandav.hearthstonecards.storage.db

import io.reactivex.Completable
import io.reactivex.Single

interface RemoteDatabase {
    fun getFavoriteCardIds(): Single<List<String>>
    fun saveFavoriteCardId(id: String): Completable
}