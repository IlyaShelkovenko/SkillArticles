/**
 * Created by Ilia Shelkovenko on 16.08.2020.
 */

package ru.skillbranch.skillarticles.viewmodels.base

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class ViewModelDelegate<T : ViewModel>(private val clazz: Class<T>, private val arg: Any?) :
    ReadOnlyProperty<FragmentActivity, T> {

    private lateinit var value: T

    override fun getValue(thisRef: FragmentActivity, property: KProperty<*>): T {
        if (!::value.isInitialized){
            value = if (arg != null) ViewModelProviders.of(thisRef, ViewModelFactory(arg)).get(clazz)
            else ViewModelProviders.of(thisRef).get(clazz)
        }
        return value
    }
}