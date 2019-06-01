package ru.karapetiandav.tinkoffintership.lifecycle

import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import androidx.lifecycle.Observer
import java.util.*

inline fun <reified T : ViewModel> Fragment.getViewModel(viewModelFactory: ViewModelProvider.Factory? = null): T {
    return ViewModelProviders.of(this, viewModelFactory)[T::class.java]
}

inline fun <reified T : Event> Fragment.observe(
    eventsQueue: EventsQueue,
    noinline block: (T) -> Unit
) {
    eventsQueue.observe(
        this.viewLifecycleOwner,
        Observer<Queue<Event>> { queue: Queue<Event>? ->
            while (queue != null && queue.isNotEmpty()) {
                block.invoke(queue.poll() as T)
            }
        }
    )
}

inline fun <reified T : Any, reified L : LiveData<T>> Fragment.observe(
    liveData: L,
    noinline block: (T) -> Unit
) {
    liveData.observe(this.viewLifecycleOwner, Observer { it?.let { block.invoke(it) } })
}

fun <T> MutableLiveData<T>.onNext(next: T) {
    this.value = next
}