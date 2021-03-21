/**
 * Created by Ilia Shelkovenko on 17.03.2021.
 */

package ru.skillbranch.skillarticles.data.remote.res

data class LikeRes(
    val message: String,
    val likeCount: Int
)

data class BookmarkRes(
    val message: String
)
