package ru.skillbranch.skillarticles.viewmodels.articles

import android.util.Log
import androidx.lifecycle.*
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.skillbranch.skillarticles.data.local.entities.ArticleItem
import ru.skillbranch.skillarticles.data.local.entities.CategoryData
import ru.skillbranch.skillarticles.data.remote.err.NoNetworkError
import ru.skillbranch.skillarticles.data.repositories.ArticlesRepository
import ru.skillbranch.skillarticles.extensions.data.toArticleFilter
import ru.skillbranch.skillarticles.viewmodels.base.BaseViewModel
import ru.skillbranch.skillarticles.viewmodels.base.IViewModelState
import ru.skillbranch.skillarticles.viewmodels.base.Notify
import java.util.concurrent.Executors


class ArticlesViewModel(handle: SavedStateHandle) :
    BaseViewModel<ArticlesState>(handle, ArticlesState()) {
    val repository = ArticlesRepository
    private var isLoadingInitial = false
    private var isLoadingAfter = false
    private val listConfig by lazy {
        PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setPageSize(10)
            .setPrefetchDistance(30)
            .setInitialLoadSizeHint(50)
            .build()
    }

    private val listData = Transformations.switchMap(state) {
        val filter = it.toArticleFilter()
        return@switchMap buildPagedList(repository.rawQueryArticles(filter))
    }


    fun observeList(
        owner: LifecycleOwner,
        isBookmark: Boolean = false,
        onChange: (list: PagedList<ArticleItem>) -> Unit
    ) {
        updateState { it.copy(isBookmark = isBookmark) }
        listData.observe(owner, Observer { onChange(it) })
    }

    fun observeTags(
        owner: LifecycleOwner,
        onChange: (list: List<String>) -> Unit
    ) {
        repository.findTags().observe(owner, Observer(onChange))
    }

    fun observeCategories(
        owner: LifecycleOwner,
        onChange: (list: List<CategoryData>) -> Unit
    ) {
        repository.findCategoriesData().observe(owner, Observer(onChange))
    }


    private fun buildPagedList(
        dataFactory: DataSource.Factory<Int, ArticleItem>
    ): LiveData<PagedList<ArticleItem>> {
        val builder = LivePagedListBuilder<Int, ArticleItem>(
            dataFactory, listConfig
        )

        if (isEmptyFiler())
            builder.setBoundaryCallback(
                ArticleBoundaryCallback(
                    ::zeroLoadingHandle,
                    ::itemAtEndHandle
                )
            )
        return builder
            .setFetchExecutor(Executors.newSingleThreadExecutor())
            .build()
    }

    private fun isEmptyFiler(): Boolean = currentState.searchQuery.isNullOrEmpty()
            && !currentState.isBookmark
            && currentState.selectedCategories.isEmpty()
            && !currentState.isHashtagSearch

    private fun itemAtEndHandle(lastLoadArticle: ArticleItem) {
        if(isLoadingAfter) return
        else isLoadingAfter = true

       launchSafety (null, {isLoadingAfter = false} ){
            repository.loadArticlesFromNetwork(
                start = lastLoadArticle.id,
                size = listConfig.pageSize
            )
        }
    }

    private fun zeroLoadingHandle() {
        if(isLoadingInitial) return
        else isLoadingInitial = true

       launchSafety(null, { isLoadingInitial = false} ) {
                repository.loadArticlesFromNetwork(start = null, size = listConfig.initialLoadSizeHint)
        }
    }

    fun handleSearch(query: String?) {
        query ?: return
        updateState { it.copy(searchQuery = query, isHashtagSearch = query.startsWith("#", true)) }
    }

    fun handleSearchMode(isSearch: Boolean) {
        updateState {
            it.copy(isSearch = isSearch)
        }
    }

    fun handleToggleBookmark(articleId: String): Unit {
       launchSafety(
           {
               when(it){
                    is NoNetworkError -> notify(Notify.TextMessage("Network not available, failed to fetch article"))
                    else ->notify(Notify.ErrorMessage(it.message ?: "Something wrong"))
               }
           }
       ) {
            val isBookmarked = repository.toggleBookmark(articleId)
            if(isBookmarked) repository.fetchArticleContent(articleId)
           // TODO else remove article content from db
        }

    }

    fun handleSuggestion(tag: String) {
        viewModelScope.launch {
            repository.incrementTagUseCount(tag)
        }
    }

    fun applyCategories(selectedCategories: List<String>) {
        updateState {
            it.copy(selectedCategories = selectedCategories)
        }
    }

    fun refresh() {
        launchSafety {
            val lastArticleId: String? = repository.findLastArticleId()
            val count = repository.loadArticlesFromNetwork(
                start = lastArticleId,
                size = if(lastArticleId == null) listConfig.initialLoadSizeHint else -listConfig.pageSize
            )
            notify(Notify.TextMessage("Load $count new articles"))
        }
    }


}

/*private fun ArticlesState.toArticleFilter(): ArticleFilter = ArticleFilter(
    search = searchQuery,
    isBookmark = isBookmark,
    categories = selectedCategories,
    isHashtag = isHashtagSearch
)*/

data class ArticlesState(
    val isSearch: Boolean = false,
    val searchQuery: String? = null,
    val isLoading: Boolean = true,
    val isBookmark: Boolean = false,
    val selectedCategories: List<String> = emptyList(),
    val isHashtagSearch: Boolean = false
) : IViewModelState


class ArticleBoundaryCallback(
    private val zeroLoadingHandle: () -> Unit,
    private val itemAtEndHandle: (ArticleItem) -> Unit

) : PagedList.BoundaryCallback<ArticleItem>() {

    override fun onZeroItemsLoaded() {
        zeroLoadingHandle()
    }

    override fun onItemAtEndLoaded(itemAtEnd: ArticleItem) {
        itemAtEndHandle(itemAtEnd)
    }
}
