package com.black.imagesearcher.base.viewmodel

import androidx.annotation.MainThread
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.black.imagesearcher.util.Log
import com.black.imagesearcher.util.Util.isNotCompleted
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * ViewModel -> View 이벤트 전송 편의를 위한 ViewModel
 */
open class EventViewModel : ViewModel()  {
    private val jobs = ConcurrentHashMap<String, Job>()
    private val event = SingleLiveData<Event>()

    @MainThread
    fun sendEvent(action: String = "", data: Any? = null) {
        event.value = Event(action, data)
    }

    /**
     * 백그라운드 스레드에서 전송 시 사용
     */
    fun postEvent(action: String = "", data: Any? = null) {
        event.postValue(Event(action, data))
    }

    @MainThread
    fun observeEvent(owner: LifecycleOwner, eventObserver: EventObserver)
        = event.observe(owner) { eventObserver.onReceivedEvent(it.action, it.data) }

    @MainThread
    fun removeEventObservers(owner: LifecycleOwner)
        = event.removeObservers(owner)

    @MainThread
    fun observeEventForever(eventObserver: EventObserver) {
        event.observeForever(object: Observer<Event> {
            override fun onChanged(value: Event) {
                eventObserver.onReceivedEvent(value.action, value.data)
            }

            override fun hashCode(): Int {
                return eventObserver.hashCode()
            }
        })
    }

    @MainThread
    fun removeEventObserver(observer: EventObserver) {
        event.removeObserver(object: Observer<Event> {
            override fun onChanged(value: Event) {
                observer.onReceivedEvent(value.action, value.data)
            }

            override fun hashCode(): Int {
                return observer.hashCode()
            }
        })
    }

    /**
     * LiveData observe -> remove 편의성 함수
     */
    fun <T> LiveData<T>.observe(observer: Observer<T>) {
        this.observeForever(observer)
        addCloseable { this.removeObserver(observer) }
    }

    fun launchSingle(
        context: CoroutineContext = EmptyCoroutineContext,
        start: CoroutineStart = CoroutineStart.DEFAULT,
        block: suspend CoroutineScope.() -> Unit
    ) {
        val methodName = Thread.currentThread().stackTrace[3].methodName
        if (jobs[methodName].isNotCompleted()) {
            Log.v("$methodName job is not completed")
            return
        }
        jobs[methodName] = viewModelScope.launch(context, start, block)
    }
}

data class Event(val action: String, val data: Any?)

fun interface EventObserver {
    fun onReceivedEvent(action: String, data: Any?)
}