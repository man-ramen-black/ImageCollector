package com.black.imagesearcher.base.data

import android.content.Context
import android.content.SharedPreferences
import com.black.imagesearcher.util.Log
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

abstract class BasePreferences(private val context: Context) {
    abstract fun getPreferences(context: Context): SharedPreferences

    fun flow(key: String, def: String): Flow<String> {
        val preferences = getPreferences(context)
        return callbackFlow {
            val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
                val value = get(key ?: return@OnSharedPreferenceChangeListener, def)
                trySend(value)
            }

            // flow 생성 후 최초에 저장되어 있는 값 전달을 위해 직접 호출
            listener.onSharedPreferenceChanged(preferences, key)

            preferences.registerOnSharedPreferenceChangeListener(listener)

            awaitClose {
                preferences.unregisterOnSharedPreferenceChangeListener(listener)
            }
        }
    }

    fun get(key: String): String? {
        return getPreferences(context).all[key]?.toString()
    }

    fun get(key: String, def: String): String {
        return (get(key) ?: def)
            .also { Log.v(it) }
    }

    fun put(key: String, value: String) {
        Log.v("$key : $value")
        getPreferences(context).edit()
            .putString(key, value)
            .apply()
    }

    fun remove(key: String) {
        Log.v(key)
        getPreferences(context).edit()
            .remove(key)
            .apply()
    }
}