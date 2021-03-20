/**
 * Created by Ilia Shelkovenko on 02.09.2020.
 */
package ru.skillbranch.skillarticles.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.*
import androidx.preference.PreferenceManager
import com.squareup.moshi.JsonAdapter
import ru.skillbranch.skillarticles.App
import ru.skillbranch.skillarticles.data.JsonConverter.moshi
import ru.skillbranch.skillarticles.data.delegates.PrefDelegate
import ru.skillbranch.skillarticles.data.delegates.PrefLiveDelegate
import ru.skillbranch.skillarticles.data.delegates.PrefLiveObjDelegate
import ru.skillbranch.skillarticles.data.delegates.PrefObjDelegate
import ru.skillbranch.skillarticles.data.models.AppSettings
import ru.skillbranch.skillarticles.data.models.User
import ru.skillbranch.skillarticles.data.models.UserJsonAdapter

object PrefManager {

    internal val preferences : SharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(App.applicationContext())
    }

    var isDarkMode by PrefDelegate(false)
    var isBigText by PrefDelegate(false)
    var accessToken by PrefDelegate("")
    var refreshToken by PrefDelegate("")
    var profile: User? by PrefObjDelegate(moshi.adapter(User::class.java))

    val isAuthLive : LiveData<Boolean> by lazy {
        val token by PrefLiveDelegate("accessToken", "", preferences)
        token.map { it.isNotEmpty() }
    }
    val profileLive : LiveData<User?> by PrefLiveObjDelegate("profile",  moshi.adapter(User::class.java), preferences)

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