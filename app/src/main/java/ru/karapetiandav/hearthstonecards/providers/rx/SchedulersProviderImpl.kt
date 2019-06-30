package ru.karapetiandav.hearthstonecards.providers.rx

import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class SchedulersProviderImpl: SchedulersProvider {
    override fun io() = Schedulers.io()
    override fun computation() = Schedulers.computation()
    override fun mainThread(): Scheduler = AndroidSchedulers.mainThread()
}