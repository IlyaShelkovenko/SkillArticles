/**
 * Created by Ilia Shelkovenko on 14.03.2021.
 */

package ru.skillbranch.skillarticles.data.remote.res

import ru.skillbranch.skillarticles.data.models.User

data class AuthRes(
    val user: User,
    val refreshToken: String,
    val accessToken: String
)