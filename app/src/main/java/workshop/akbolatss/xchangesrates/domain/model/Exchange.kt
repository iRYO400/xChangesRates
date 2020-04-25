package workshop.akbolatss.xchangesrates.domain.model

import java.util.*

data class Exchange(
    val id: String,
    val caption: String,
    val currencies: Map<String, List<String>>,
    val updateTime: Date
)
