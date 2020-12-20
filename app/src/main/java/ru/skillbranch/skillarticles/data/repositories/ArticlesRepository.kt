package ru.skillbranch.skillarticles.data.repositories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.paging.PositionalDataSource
import ru.skillbranch.skillarticles.data.models.ArticleItemData
import ru.skillbranch.skillarticles.data.LocalDataHolder


object ArticlesRepository {

    private val local = LocalDataHolder

    fun allArticles(): ArticleDataFactory = ArticleDataFactory(ArticleStrategy.AllArticles(::findArticlesByRange))

    private fun findArticlesByRange(start: Int, size: Int) = local.localArticleItems
        .drop(start)
        .take(size)
}

class ArticleDataFactory(private val strategy : ArticleStrategy) :
    DataSource.Factory<Int, ArticleItemData>() {
    override fun create(): DataSource<Int, ArticleItemData> = ArticleDataSource(strategy)

}

class ArticleDataSource(val strategy: ArticleStrategy) : PositionalDataSource<ArticleItemData>(){
    override fun loadInitial(
        params: LoadInitialParams,
        callback: LoadInitialCallback<ArticleItemData>
    ) {
        val result = strategy.getItems(params.requestedStartPosition, params.requestedLoadSize)
        Log.e("ArticlesRepository","loadInitial: start > ${params.requestedStartPosition} " +
                "size > ${params.requestedLoadSize} resultSize ? ${result.size}")
        callback.onResult(result, params.requestedStartPosition)
    }

    override fun loadRange(params: LoadRangeParams, callback: LoadRangeCallback<ArticleItemData>) {
        val result = strategy.getItems(params.startPosition, params.loadSize)
        Log.e("ArticlesRepository","loadRange: start > ${params.startPosition} size > ${params.loadSize}, resultSize ? ${result.size}")
        callback.onResult(result)
    }

}

sealed class ArticleStrategy() {
    abstract fun getItems(start: Int, size: Int) : List<ArticleItemData>

    class AllArticles(
        private val itemProvider: (Int, Int) -> List<ArticleItemData>
    ) : ArticleStrategy(){
        override fun getItems(start: Int, size: Int): List<ArticleItemData> = itemProvider(start, size)

    }

    class SearchArticle(
        private val itemProvider: (Int, Int, String) -> List<ArticleItemData>,
        private val query: String
    ) : ArticleStrategy(){
        override fun getItems(start: Int, size: Int): List<ArticleItemData> = itemProvider(start,size, query)

    }
}