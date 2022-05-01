package org.hyperskill.musicplayer

fun Int.secondsToTimeString(): String {
    val seconds = "%02d".format(this % 60)
    val minutes = "%02d".format(this / 60)
    return "$minutes:$seconds"
}