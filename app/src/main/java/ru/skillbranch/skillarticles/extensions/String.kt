/**
 * Created by Ilia Shelkovenko on 15.08.2020.
 */
package ru.skillbranch.skillarticles.extensions

fun String?.indexesOf(substr: String, ignoreCase: Boolean = true): List<Int>{
    if(substr.isEmpty() || this.isNullOrEmpty()) return emptyList()
    val listOfIndexes = mutableListOf<Int>()
    var currIndex : Int = 0
    while(currIndex != -1) {
        currIndex = this.indexOf(substr, currIndex, ignoreCase);

        if(currIndex != -1){
            listOfIndexes.add(currIndex);
            currIndex += 1;
        }
    }
    return listOfIndexes
}