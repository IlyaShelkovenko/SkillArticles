/**
 * Created by Ilia Shelkovenko on 02.09.2020.
 */
package ru.skillbranch.skillarticles.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.distinctUntilChanged
import androidx.preference.PreferenceManager
import ru.skillbranch.skillarticles.App
import ru.skillbranch.skillarticles.data.delegates.PrefDelegate
import ru.skillbranch.skillarticles.data.delegates.PrefLiveDelegate
import ru.skillbranch.skillarticles.data.models.AppSettings

object PrefManager {
    internal val preferences : SharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(App.applicationContext())
    }

    var isAuth by PrefDelegate(false)
    var isDarkMode by PrefDelegate(false)
    var isBigText by PrefDelegate(false)

    val isAuthLive : LiveData<Boolean> by PrefLiveDelegate("isAuth", false, preferences)

    val appSettings: LiveData<AppSettings> = MediatorLiveData<AppSettings>().apply {
        val isDarkModeLive: LiveData<Boolean> by PrefLiveDelegate("isDarkMode", false, preferences)
        val isBigTextLive: LiveData<Boolean> by PrefLiveDelegate("isDarkMode", false, preferences)
        value = AppSettings()

        addSource(isDarkModeLive){
            value = value!!.copy(isDarkMode = it)
        }

        addSource(isBigTextLive){
            value = value!!.copy(isBigText = it)
        }
    }.distinctUntilChanged()

    fun clearAll(){
        preferences.edit().clear().apply()
    }


    fun updateSettings(appSettings: AppSettings) {
        isDarkMode = appSettings.isDarkMode
        isBigText = appSettings.isBigText
    }

    fun isAuth(): MutableLiveData<Boolean> {
        //TODO implement me
        return MutableLiveData(false)
    }

    fun setAuth(auth: Boolean): Unit {
        // TODO implement me
    }
}