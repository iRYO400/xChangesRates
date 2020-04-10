package workshop.akbolatss.xchangesrates.utils.extension

import java.math.BigDecimal

fun BigDecimal?.toFormattedString(): String {
    if (this == null)
        return "0"

    val scaled = setScale(4)

    return scaled.toPlainString()
}
