/**
 * Created by Ilia Shelkovenko on 27.09.2020.
 */
package ru.skillbranch.skillarticles.extensions

fun List<Pair<Int, Int>>.groupByBounds(bounds: List<Pair<Int, Int>>) : List<List<Pair<Int, Int>>> {
    val resultList = mutableListOf<List<Pair<Int, Int>>>()
    bounds.forEach {(leftBound, rightBound) ->
        val inBounds = this.filter { (lb, rb) ->
            lb >= leftBound && rb <= rightBound
        }
        resultList.add(inBounds)
    }
    return resultList
}
