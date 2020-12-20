package ru.skillbranch.skillarticles.extensions

import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs


const val SECOND = 1000L
const val MINUTE = 60 * SECOND
const val HOUR = 60 * MINUTE
const val DAY = 24 * HOUR
const val SECONDS_IN_MINUTE = 60
const val SECONDS_IN_HOUR = SECONDS_IN_MINUTE * 60
const val SECONDS_IN_DAY = SECONDS_IN_HOUR * 24

fun Date.format(pattern: String = "HH:mm:ss dd.MM.yy"): String {
    val dateFormat = SimpleDateFormat(pattern, Locale("ru"))
    return dateFormat.format(this)
}

fun Date.shortFormat(): String {
    val pattern = if(this.isSameDay(Date())) "HH:mm" else "dd.MM.yy"
    val dateFormat = SimpleDateFormat(pattern, Locale("ru"))
    return  dateFormat.format(this)
}

fun Date.isSameDay(date: Date): Boolean {
    val day1 = this.time / DAY
    val day2 = date.time / DAY
    return  day1 == day2
}

fun Date.add(value: Int, units: TimeUnits = TimeUnits.SECOND) : Date {
    var time = this.time

    time += when(units){
        TimeUnits.SECOND -> value * SECOND
        TimeUnits.MINUTE -> value * MINUTE
        TimeUnits.HOUR -> value * HOUR
        TimeUnits.DAY -> value * DAY
    }
    this.time = time

    return this
}

enum class TimeUnits {
    SECOND{
        override fun plural(value: Int): String {
            var correctForm = if (value in 5..20) {
                "секунд"
            } else
                when (value.toString().last()) {
                    '1' -> "секунду"
                    '2', '3', '4' -> "секунды"
                    '0', '5', '6', '7', '8', '9' -> "секунд"
                    else -> "неизвестное число"
                }
            return "${abs(value)} $correctForm"
        }
    },
    MINUTE {
        override fun plural(value: Int): String {
            val correctForm = if(value in 5..20){
                "минут"
            }
            else
                when(value.toString().last()){
                    '1' -> "минуту"
                    '2','3','4' -> "минуты"
                    '0','5','6','7','8','9' -> "минут"
                    else -> "неизвестное число"
                }
            return "${abs(value)} $correctForm"
        }
    },
    HOUR {
        override fun plural(value: Int): String {
            val correctForm = if(value in 5..20){
                "часов"
            }
            else
                when(value.toString().last()){
                    '1' -> "час"
                    '2','3','4' -> "часа"
                    '0','5','6','7','8','9' -> "часов"
                    else -> "неизвестное число"
                }
            return "${abs(value)} $correctForm"
        }
    },
    DAY {
        override fun plural(value: Int): String {
            val correctForm = if(value in 5..20){
                "дней"
            }
            else
                when(value.toString().last()){
                    '1' -> "день"
                    '2','3','4' -> "дня"
                    '0','5','6','7','8','9' -> "дней"
                    else -> "неизвестное число"
                }
            return "${abs(value)} $correctForm"
        }
    };

    abstract fun plural(value: Int): String
}