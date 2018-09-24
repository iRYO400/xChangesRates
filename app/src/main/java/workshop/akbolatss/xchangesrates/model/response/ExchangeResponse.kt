package workshop.akbolatss.xchangesrates.model.response

import workshop.akbolatss.xchangesrates.model.ExchangeModel

data class ExchangeResponse(
        val request: String,
        val data: List<ExchangeModel>
)
