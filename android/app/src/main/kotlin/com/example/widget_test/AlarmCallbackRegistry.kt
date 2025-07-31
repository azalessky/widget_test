package com.example.widget_test

object AlarmCallbackRegistry {
    private val callbacks = mutableMapOf<String, () -> Unit>()

    fun getKeys(): Set<String> = callbacks.keys

    fun register(key: String, callback: () -> Unit) {
        callbacks[key] = callback
    }

    fun remove(key: String) {
        callbacks.remove(key)
    }

    fun trigger(key: String) {
        callbacks.remove(key)?.invoke()
    }
    
    fun clear() {
        callbacks.clear()
    }
}