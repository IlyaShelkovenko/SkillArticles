/**
 * Created by Ilia Shelkovenko on 16.08.2020.
 */

package ru.skillbranch.skillarticles.data.local

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import androidx.preference.PreferenceManager
import ru.skillbranch.skillarticles.data.delegates.PrefDelegate


class PrefManager(context: Context) {
    val preferences : SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    var storedBoolean by PrefDelegate(false)
    var storedString by PrefDelegate("")
    var storedFloat by PrefDelegate(0f)
    var storedInt by PrefDelegate(0)
    var storedLong by PrefDelegate(0)

    fun clearAll(){
        val edit: Editor = preferences.edit()
        edit.clear().apply()
    }
}