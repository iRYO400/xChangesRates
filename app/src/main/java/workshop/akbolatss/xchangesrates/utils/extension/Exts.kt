package workshop.akbolatss.xchangesrates.utils.extension

import workshop.akbolatss.xchangesrates.domain.model.Snapshot
import java.util.*

fun String.Companion.empty() = ""

fun defaultVal() = -1L
fun localId() = 0L
fun emptyDate() = Date(defaultVal())

