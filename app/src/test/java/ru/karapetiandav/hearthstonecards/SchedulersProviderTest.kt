package ru.karapetiandav.hearthstonecards

import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers
import ru.karapetiandav.hearthstonecards.providers.rx.SchedulersProvider

class SchedulersProviderTest: SchedulersProvider {
    override fun io() = Schedulers.trampoline()

    override fun computation() = Schedulers.trampoline()

    override fun mainThread() = Schedulers.trampoline()
}