package workshop.akbolatss.xchangesrates.data.mapper

import workshop.akbolatss.xchangesrates.data.persistent.model.ExchangeEntity
import workshop.akbolatss.xchangesrates.data.remote.model.ExchangeResponse
import workshop.akbolatss.xchangesrates.domain.model.Exchange
import java.util.*

object ExchangeResponseMap {

    fun ExchangeResponse.map() = ExchangeEntity(
        id = ident,
        caption = caption,
        currencies = currencies,
        updateTime = Date()
    )
}

object ExchangeEntityMap {

    fun ExchangeEntity.map() = Exchange(
        id = id,
        caption = caption,
        currencies = currencies,
        updateTime = Date()
    )
}
