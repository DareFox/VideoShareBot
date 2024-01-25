package me.darefox.videosharebot.extensions

fun pow(num: Int, exp: Int): Long {
    var result: Long = num.toLong()
    repeat(exp) {
        result *= num
    }

    return result
}