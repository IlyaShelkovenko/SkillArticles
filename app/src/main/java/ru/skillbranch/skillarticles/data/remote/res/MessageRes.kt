/**
 * Created by Ilia Shelkovenko on 16.03.2021.
 */

package ru.skillbranch.skillarticles.data.remote.res

data class MessageRes(
    val message: CommentRes,
    val messageCount : Int
)