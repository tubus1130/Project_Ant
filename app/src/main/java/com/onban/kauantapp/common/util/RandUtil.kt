package com.onban.kauantapp.common.util

import java.util.*

object RandUtil {
    fun getRandInt(from: Int, end: Int): Int {
        val rand = Random()
        return rand.nextInt(end - from + 1) + from
    }
}