/**
 * Created by Ilia Shelkovenko on 06.03.2021.
 */

package ru.skillbranch.skillarticles.data.delegates

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class PrefLiveDelegate<T> (
    private val fieldKey : String,
    private val defaultValue: T,
    private val preferences : SharedPreferences
): ReadOnlyProperty <Any?, LiveData<T>>{
    private var storedValue: LiveData<T>? = null
    override fun getValue(thisRef: Any?, property: KProperty<*>): LiveData<T> {
        if(storedValue == null) {
            storedValue = SharedPreferencesLiveData(preferences, fieldKey, defaultValue)
        }
        return storedValue!!
    }
}

internal class SharedPreferencesLiveData<T>(
    var sharedPreferences: SharedPreferences,
    var key: String,
    var defaultValue: T
): LiveData<T>() {
    private val preferencesChangeListener = SharedPreferences.OnSharedPreferenceChangeListener { _, shKey ->
        if(shKey == key){
            value = readValue(defaultValue)
        }
    }

    override fun onActive() {
        super.onActive()
        value = readValue(defaultValue)
        sharedPreferences.registerOnSharedPreferenceChangeListener(preferencesChangeListener)
    }

    override fun onInactive() {
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(preferencesChangeListener)
        super.onInactive()
    }

    private fun readValue(defaultValue: T) : T {
        return when(defaultValue){
            is Int -> sharedPreferences.getInt(key, defaultValue as Int) as T
            is Long -> sharedPreferences.getLong(key, defaultValue as Long) as T
            is Float -> sharedPreferences.getFloat(key, defaultValue as Float) as T
            is String -> sharedPreferences.getString(key, defaultValue as String) as T
            is Boolean -> sharedPreferences.getBoolean(key, defaultValue as Boolean) as T
            else -> error("This type $defaultValue can not be stored into Preferences")
        }
    }
}