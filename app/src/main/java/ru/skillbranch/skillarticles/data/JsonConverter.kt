package ru.skillbranch.skillarticles.data

import com.squareup.moshi.FromJson
import com.squareup.moshi.Moshi
import com.squareup.moshi.ToJson
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.util.*

object JsonConverter {
    //json converter
    val moshi = Moshi.Builder()
        .add(DateAdapter())
        .add(KotlinJsonAdapterFactory()) // convert json to class by reflection
        .build()

    class DateAdapter {
        @ToJson
        fun toJson(date: Date) = date.time

        @FromJson
        fun fromJson(timestamp: Long) = Date(timestamp)
    }
}