/**
 * Created by Ilia Shelkovenko on 07.02.2021.
 */
package ru.skillbranch.skillarticles.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import ru.skillbranch.skillarticles.data.local.entities.ArticleContent

@Dao
interface ArticleContentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(obj : ArticleContent): Long
}