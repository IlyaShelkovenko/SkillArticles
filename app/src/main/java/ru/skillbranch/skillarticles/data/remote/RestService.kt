/**
 * Created by Ilia Shelkovenko on 09.03.2021.
 */
package ru.skillbranch.skillarticles.data.remote

import retrofit2.Call
import retrofit2.http.*
import ru.skillbranch.skillarticles.data.local.entities.Article
import ru.skillbranch.skillarticles.data.local.entities.ArticleContent
import ru.skillbranch.skillarticles.data.remote.req.LoginReq
import ru.skillbranch.skillarticles.data.remote.req.MessageReq
import ru.skillbranch.skillarticles.data.remote.res.*

interface RestService {
    @GET("articles")
    suspend fun articles(
        @Query("last") last: String?,
        @Query("limit") limit: Int = 10
    ): List<ArticleRes>

    @GET("articles/{article}/content")
    suspend fun loadArticleContent(@Path("article") articleId: String): ArticleContentRes

    @GET("articles/{article}/messages")
    fun loadComments(
        @Path("article") articleId: String,
        @Query("last") last: String? = null,
        @Query("limit") limit: Int = 5
    ): Call<List<CommentRes>>

    @POST("articles/{article}/messages")
    suspend fun sendMessage(
        @Path("article") articleId: String,
        @Body message: MessageReq,
        @Header("Authorization") token: String
    ): MessageRes

    @GET("articles/{article}/counts")
    suspend fun loadArticleCounts(@Path("article") articleId: String): ArticleCountsRes

    @POST("auth/login")
    suspend fun login(@Body loginReq: LoginReq) : AuthRes

    @POST("articles/{article}/decrementLikes")
    suspend fun decrementLike(
        @Path("article")articleId: String,
        @Header("Authorization") token: String
    ): LikeRes

    @POST("articles/{article}/incrementLikes")
    suspend fun incrementLike(
        @Path("article")articleId: String,
        @Header("Authorization") token: String
    ): LikeRes


}