/**
 * Created by Ilia Shelkovenko on 16.08.2020.
 */

package ru.skillbranch.skillarticles.data.delegates

import ru.skillbranch.skillarticles.data.local.PrefManager
import java.lang.IllegalArgumentException
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class PrefDelegate<T>(private val defaultValue: T) : ReadWriteProperty<PrefManager, T?> {
    private var value: T? = null

    override fun getValue(thisRef: PrefManager, property: KProperty<*>): T? {
        /*if(value == null) {
            value = when (defaultValue) {
                is Boolean -> thisRef.preferences.getBoolean(property.name,defaultValue) as T
                is String -> thisRef.preferences.getString(property.name,defaultValue) as T
                is Float -> thisRef.preferences.getFloat(property.name,defaultValue) as T
                is Int -> thisRef.preferences.getInt(property.name,defaultValue) as T
                is Long -> thisRef.preferences.getLong(property.name,defaultValue) as T
                else -> throw IllegalArgumentException("Unknown type")
    }
}*/
        return value
    }

    override fun setValue(thisRef: PrefManager, property: KProperty<*>, value: T?) {
        TODO("Not yet implemented")
    }

}