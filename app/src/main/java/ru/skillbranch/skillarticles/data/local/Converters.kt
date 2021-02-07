/**
 * Created by Ilia Shelkovenko on 04.02.2021.
 */
package ru.skillbranch.skillarticles.data.local

import androidx.room.TypeConverter
import java.sql.Timestamp
import java.util.*

class DateConverter {
    @TypeConverter
    fun timestampToDate(timestamp: Long) : Date = Date(timestamp)

    @TypeConverter
    fun dateToTimestamp(date: Date): Long = date.time
}