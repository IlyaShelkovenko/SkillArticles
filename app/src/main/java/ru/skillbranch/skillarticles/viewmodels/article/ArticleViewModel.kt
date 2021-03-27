/**
 * Created by Ilia Shelkovenko on 01.08.2020.
 */

package ru.skillbranch.skillarticles.viewmodels.article

import androidx.lifecycle.*
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import kotlinx.coroutines.launch
import ru.skillbranch.skillarticles.data.remote.res.CommentRes
import ru.skillbranch.skillarticles.data.repositories.*
import ru.skillbranch.skillarticles.extensions.data.toAppSettings
import ru.skillbranch.skillarticles.extensions.indexesOf
import ru.skillbranch.skillarticles.extensions.shortFormat
import ru.skillbranch.skillarticles.viewmodels.base.BaseViewModel
import ru.skillbranch.skillarticles.viewmodels.base.IViewModelState
import ru.skillbranch.skillarticles.viewmodels.base.NavigationCommand
import ru.skillbranch.skillarticles.viewmodels.base.Notify
import java.util.concurrent.Executors

class ArticleViewModel(
    handle: SavedStateHandle,
    private val articleId: String
) : BaseViewModel<ArticleState>(handle, ArticleState()), IArticleViewModel {
    private val repository = ArticleRepository
    private var clearContent: String? = null
    private val listConfig by lazy {
        PagedList.Config.Builder()
            .setEnablePlaceholders(true)
            .setPageSize(5)
            .build()
    }

    private val listData: LiveData<PagedList<CommentRes>> =
        Transformations.switchMap(repository.findArticleCommentCount(articleId)) {
            buildPageList(repository.loadAllComments(articleId, it, ::commentLoadErrorHandler))
        }

    init {
        subscribeOnDataSource(repository.findArticle(articleId)) { article, state ->
            if (article.content == null) fetchContent()
            state.copy(
                shareLink = article.shareLink,
                title = article.title,
                category = article.category.title,
                categoryIcon = article.category.icon,
                date = article.date.shortFormat(),
                author = article.author,
                isBookmark = article.isBookmark,
                isLike = article.isLike,
                content = article.content ?: emptyList(),
                isLoadingContent = article.content == null,
                source = article.source,
                hashtags = article.tags
            )
        }

        subscribeOnDataSource(repository.getAppSettings()) { settings, state ->
            state.copy(
                isDarkMode = settings.isDarkMode,
                isBigText = settings.isBigText
            )
        }
        subscribeOnDataSource(repository.isAuth()) { auth, state ->
            state.copy(isAuth = auth)
        }
    }
    fun refresh(){
        launchSafety {
            launch { repository.fetchArticleContent(articleId) }
            launch { repository.refreshCommentsCount(articleId) }
        }
    }

    private fun commentLoadErrorHandler(throwable: Throwable) {
        // TODO handle network errors
    }

    private fun fetchContent() {
        launchSafety {
            repository.fetchArticleContent(articleId)
        }
    }

    override fun handleUpText() {
        repository.updateSettings(currentState.toAppSettings().copy(isBigText = true))
    }

    override fun handleDownText() {
        repository.updateSettings(currentState.toAppSettings().copy(isBigText = false))
    }

    override fun handleNightMode() {
        val settings = currentState.toAppSettings()
        repository.updateSettings(
            currentState.toAppSettings().copy(isDarkMode = !settings.isDarkMode)
        )
    }

    override fun handleLike() {
        val isLiked = currentState.isLike
        val msg = if (isLiked) {
            Notify.ActionMessage(
                "Don`t like it anymore",
                "No, still like it"
            ) {
                handleLike()
            }
        } else {
            Notify.TextMessage("Mark is liked")

        }
        launchSafety(null, { notify(msg) }) {
            repository.toggleLike(articleId)
            if (isLiked) repository.decrementLike(articleId) else repository.incrementLike(articleId)
        }
    }

    override fun handleBookmark() {
        val msg = if (currentState.isBookmark) "Remove from bookmarks"  else "Add to bookmarks"
       launchSafety(null, {notify(Notify.TextMessage(msg)) } ) {
            repository.toggleBookmark(articleId)
        }

    }

    override fun handleShare() {
        val msg = "Share is not implemented"
        notify(Notify.ErrorMessage(msg, "OK", null))
    }

    override fun handleToggleMenu() {
        updateState {
            it.copy(isShowMenu = !it.isShowMenu)
        }
    }

    override fun handleSearchMode(isSearch: Boolean) {
        updateState {
            it.copy(isSearch = isSearch, isShowMenu = false, searchPosition = 0)
        }
    }

    override fun handleSearch(query: String?) {
        query ?: return
        if (clearContent == null && currentState.content.isNotEmpty()) clearContent =
            currentState.content.clearContent()
        val result = clearContent
            .indexesOf(query)
            .map { it to it + query.length }
        updateState {
            it.copy(searchQuery = query, searchResults = result, searchPosition = 0)
        }
    }

    override fun handleUpResult() {
        updateState { it.copy(searchPosition = it.searchPosition.dec()) }
    }

    override fun handleDownResult() {
        updateState { it.copy(searchPosition = it.searchPosition.inc()) }
    }

    override fun handleCopyCode() {
        notify(Notify.TextMessage("Copy code to clipboard"))
    }

    override fun handleSendComment(comment: String) {
        if(comment == null){
            notify(Notify.TextMessage("Comment must be not empty"))
            return
        }
        updateState { it.copy(comment = comment) }
        if (!currentState.isAuth) {
            navigate(NavigationCommand.StartLogin())
        } else {
            launchSafety(null,{
                updateState {
                    it.copy(
                    answerTo = null,
                    answerToMessageId = null,
                    comment = null
                    )
                }
            }){
                repository.sendMessage(articleId, comment, currentState.answerToMessageId)

            }
        }

    }

    fun observeList(
        owner: LifecycleOwner,
        onChange: (list: PagedList<CommentRes>) -> Unit
    ) {
        listData.observe(owner, Observer {
            onChange(it)
        })
    }

    private fun buildPageList(
        dataFactory: CommentsDataFactory
    ): LiveData<PagedList<CommentRes>> {
        return LivePagedListBuilder<String, CommentRes>(
            dataFactory,
            listConfig
        )
            .setFetchExecutor(Executors.newSingleThreadExecutor())
            .build()
    }

    fun handleCommentFocus(hasFocus: Boolean) {
        updateState { it.copy(showBottomBar = !hasFocus) }
    }

    fun handleClearComment() {
        updateState { it.copy(answerTo = null, answerToMessageId = null) }
    }

    fun handleReplyTo(messageId: String, name: String) {
        updateState { it.copy(answerToMessageId = messageId, answerTo = "Reply to $name") }
    }


}

data class ArticleState(
    val isAuth: Boolean = false,
    val isLoadingContent: Boolean = true,
    val isLoadingReviews: Boolean = true,
    val isLike: Boolean = false,
    val isBookmark: Boolean = false,
    val isShowMenu: Boolean = false,
    val isBigText: Boolean = false,
    val isDarkMode: Boolean = false,
    val isSearch: Boolean = false,
    val searchQuery: String? = null,
    val searchResults: List<Pair<Int, Int>> = emptyList(),
    val searchPosition: Int = 0,
    val shareLink: String? = null,
    val title: String? = null,
    val category: String? = null,
    val categoryIcon: Any? = null,
    val date: String? = null,
    val author: Any? = null,
    val poster: String? = null,
    val content: List<MarkdownElement> = emptyList(),
    val commentsCount: Int = 0,
    val answerTo: String? = null,
    val answerToMessageId: String? = null,
    val showBottomBar: Boolean = true,
    val comment: String? = null,
    val source: String? = null,
    val hashtags: List<String> = emptyList()
) : IViewModelState {

    override fun save(outState: SavedStateHandle) {
        outState.set("isSearch", isSearch)
        outState.set("searchQuery", searchQuery)
        outState.set("searchResults", searchResults)
        outState.set("searchPosition", searchPosition)
        outState.set("comment", comment)
        outState.set("answerTo", answerTo)
        outState.set("answerToMessageId", answerToMessageId)

    }

    override fun restore(savedState: SavedStateHandle): IViewModelState {
        return copy(
            isSearch = savedState["isSearch"] ?: false,
            searchQuery = savedState["searchQuery"],
            searchResults = savedState["searchResults"] ?: emptyList(),
            searchPosition = savedState["searchPosition"] ?: 0,
            comment = savedState["comment"],
            answerTo = savedState["answerTo"],
            answerToMessageId = savedState["answerToMessageId"]
        )
    }
}