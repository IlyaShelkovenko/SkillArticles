/**
 * Created by Ilia Shelkovenko on 09.03.2021.
 */
package ru.skillbranch.skillarticles.data.remote

import retrofit2.http.GET
import retrofit2.http.Query
import ru.skillbranch.skillarticles.data.local.entities.Article
import ru.skillbranch.skillarticles.data.remote.res.ArticleRes

interface RestService {
    @GET("articles")
    suspend fun articles(
        @Query("last") last: String?,
        @Query("limit") limit: Int = 10
    ): List<ArticleRes>
}