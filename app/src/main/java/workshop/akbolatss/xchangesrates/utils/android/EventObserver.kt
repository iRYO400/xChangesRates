package kz.jgroup.pos.util

import androidx.lifecycle.Observer

/**
 * Custom [Observer] for [Event] wrapped objects when using with LiveData
 * @author Akbolat. Date 18.07.2019
 */
class EventObserver<T>(private val onEventUnhandledContent: (T) -> Unit) : Observer<Event<T>> {
    override fun onChanged(event: Event<T>?) {
        event?.getContentIfNotHandled()?.let { value ->
            onEventUnhandledContent(value)
        }
    }
}