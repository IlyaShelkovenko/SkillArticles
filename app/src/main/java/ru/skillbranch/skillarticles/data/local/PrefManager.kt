/**
 * Created by Ilia Shelkovenko on 02.09.2020.
 */
package ru.skillbranch.skillarticles.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.preference.PreferenceManager
import ru.skillbranch.skillarticles.App
import ru.skillbranch.skillarticles.data.delegates.PrefDelegate
import ru.skillbranch.skillarticles.data.models.AppSettings

object PrefManager {
    internal val preferences : SharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(App.applicationContext())
    }

    var isDarkMode by PrefDelegate(false)
    var isBigText by PrefDelegate(false)

    fun clearAll(){
        preferences.edit().clear().apply()
    }

    fun getAppSettings(): LiveData<AppSettings> {
        //TODO implement me
        return MutableLiveData(AppSettings())
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