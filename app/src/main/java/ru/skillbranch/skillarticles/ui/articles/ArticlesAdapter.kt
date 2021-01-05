/**
 * Created by Ilia Shelkovenko on 01.11.2020.
 */

package ru.skillbranch.skillarticles.ui.articles

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.extensions.LayoutContainer
import ru.skillbranch.skillarticles.R
import ru.skillbranch.skillarticles.data.models.ArticleItemData
import ru.skillbranch.skillarticles.ui.custom.ArticleItemView

class ArticlesAdapter(
    private val listener: (ArticleItemData) -> Unit,
    private val toggleBookmarkListener : (articleId: String, isBookmark: Boolean) -> Unit
) : PagedListAdapter<ArticleItemData, ArticleVH>(ArticleDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleVH {
        val view = ArticleItemView(parent.context)
        return ArticleVH(view)
    }

    override fun onBindViewHolder(holder: ArticleVH, position: Int) {
        holder.bind(getItem(position), listener, toggleBookmarkListener)
    }

}

class ArticleDiffCallback : DiffUtil.ItemCallback<ArticleItemData>() {

    override fun areItemsTheSame(oldItem: ArticleItemData, newItem: ArticleItemData): Boolean = oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: ArticleItemData, newItem: ArticleItemData): Boolean = oldItem == newItem
}

class ArticleVH(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer {

    fun bind(
        item : ArticleItemData?,
        listener:  (ArticleItemData) -> Unit,
        toggleBookmarkListener : (articleId: String, isBookmark: Boolean) -> Unit
    ) {
        //if use place holder item may be null
        (containerView as ArticleItemView).bind(item!!, toggleBookmarkListener)
        itemView.setOnClickListener { listener(item!!) }
    }
}