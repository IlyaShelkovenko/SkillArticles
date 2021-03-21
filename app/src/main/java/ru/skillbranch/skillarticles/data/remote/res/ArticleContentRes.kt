/**
 * Created by Ilia Shelkovenko on 09.03.2021.
 */

package ru.skillbranch.skillarticles.data.remote.res

import java.util.*

data class ArticleContentRes(
    val articleId: String,
    val content: String,
    val source: String? = null,
    val shareLink: String,
    val updatedAt: Date = Date()
)