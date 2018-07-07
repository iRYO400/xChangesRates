package workshop.akbolatss.xchangesrates.model.response

import workshop.akbolatss.xchangesrates.model.ExchangeModel

/**
 * Author: Akbolat Sadvakassov
 * Date: 08.12.2017
 */

data class ExchangeResponse(
        val request: String,
        val data: List<ExchangeModel>
)
