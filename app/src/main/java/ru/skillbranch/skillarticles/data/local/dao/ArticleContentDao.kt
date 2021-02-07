/**
 * Created by Ilia Shelkovenko on 07.02.2021.
 */
package ru.skillbranch.skillarticles.data.local.dao

import androidx.room.Dao
import ru.skillbranch.skillarticles.data.local.entities.ArticleContent

@Dao
interface ArticleContentDao : BaseDao<ArticleContent>{
}