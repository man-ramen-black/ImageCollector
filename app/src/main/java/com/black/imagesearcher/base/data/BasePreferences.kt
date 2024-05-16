package com.black.imagesearcher.base.data

import android.content.Context
import android.content.SharedPreferences
import com.black.imagesearcher.util.JsonUtil
import com.black.imagesearcher.util.Log
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

abstract class BasePreferences(private val context: Context) {
    abstract fun getPreferences(context: Context): SharedPreferences

    fun <T> flow(key: String, def: T, cls: Class<T>): Flow<T> {
        val preferences = getPreferences(context)
        return callbackFlow {
            val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
                val value = get(key ?: return@OnSharedPreferenceChangeListener, def, cls)
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

    fun <T> get(key: String, cls: Class<T>): T? {
        val value = get(key)
        return when (cls.name) {
            Int::class.java.name -> cls.cast(value?.toIntOrNull() ?: return null)
            Long::class.java.name -> cls.cast(value?.toLongOrNull() ?: return null)
            Float::class.java.name -> cls.cast(value?.toFloatOrNull() ?: return null)
            Boolean::class.java.name -> cls.cast(value?.toBoolean() ?: return null)
            String::class.java.name -> cls.cast(value ?: return null)
            else -> JsonUtil.from(value ?: return null, cls)
        }
    }

    fun <T> get(key: String, def: T, cls: Class<T>): T {
        return get(key, cls) ?: def
    }

    fun <T> put(key: String, value: T, cls: Class<T>) {
        getPreferences(context).edit()
            .apply {
                when (value) {
                    is Int -> putInt(key, value)
                    is Long -> putLong(key, value)
                    is Float -> putFloat(key, value)
                    is Boolean -> putBoolean(key, value)
                    is String -> putString(key, value)
                    else -> putString(key, JsonUtil.to(value, cls) ?: return@apply)
                }
            }
            .apply()
    }

    fun remove(key: String) {
        Log.v(key)
        getPreferences(context).edit()
            .remove(key)
            .apply()
    }
}