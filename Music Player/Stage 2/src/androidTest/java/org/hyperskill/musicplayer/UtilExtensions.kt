package org.hyperskill.musicplayer

/**
 * consider this value to be an amount of seconds and returns a sting with the time representation of
 * that amount in "clock format", but minutes are not bound to 2 digits (ex: "05:43", "00:00" "100:59")
 */
fun Int.secondsToTimeString(): String {
    val seconds = "%02d".format(this % 60)
    val minutes = "%02d".format(this / 60)
    return "$minutes:$seconds"
}