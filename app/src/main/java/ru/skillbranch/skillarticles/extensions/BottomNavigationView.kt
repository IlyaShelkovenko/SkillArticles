/**
 * Created by Ilia Shelkovenko on 05.11.2020.
 */
package ru.skillbranch.skillarticles.extensions

import androidx.navigation.NavDestination
import com.google.android.material.bottomnavigation.BottomNavigationView
import ru.skillbranch.skillarticles.R

fun BottomNavigationView.selectDestination(dest: NavDestination) {
    val item = menu.findItem(dest.id)

    if(item != null)
        item.isChecked = true
    else {
        menu.findItem(R.id.nav_profile).isChecked = true
    }
}