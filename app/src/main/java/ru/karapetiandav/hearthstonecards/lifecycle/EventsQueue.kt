package ru.karapetiandav.tinkoffintership.lifecycle

import androidx.annotation.MainThread
import androidx.lifecycle.MutableLiveData
import ru.karapetiandav.hearthstonecards.lifecycle.Event
import java.util.*

class EventsQueue : MutableLiveData<Queue<Event>>() {
    @MainThread
    fun offer(event: Event) {
        val queue = value ?: LinkedList()
        queue.add(event)
        value = queue
    }

}