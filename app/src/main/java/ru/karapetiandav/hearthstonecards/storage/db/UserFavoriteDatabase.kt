package ru.karapetiandav.hearthstonecards.storage.db

import com.androidhuman.rxfirebase2.database.data
import com.androidhuman.rxfirebase2.database.rxSetValue
import io.reactivex.Completable
import io.reactivex.Single

class UserFavoriteDatabase(
    private val usersFavoriteReferenceProvider: UserFavoriteReferenceProvider
) : RemoteDatabase {
    override fun getFavoriteCardIds(): Single<List<String>> {
        return usersFavoriteReferenceProvider.getReference()
            ?.data()
            ?.flatMap { snapshot ->
                Single.just(snapshot.children.toList().map { it.key ?: "" })
            } ?: Single.just(listOf())
    }

    override fun saveFavoriteCardId(id: String): Completable {
        return usersFavoriteReferenceProvider.getReference()
            ?.child(id)
            ?.rxSetValue(true) ?: Completable.complete()
    }
}

