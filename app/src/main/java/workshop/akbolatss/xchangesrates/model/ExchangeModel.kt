package workshop.akbolatss.xchangesrates.model

/**
 * Author: Akbolat Sadvakassov
 * Date: 09.12.2017
 */

data class ExchangeModel(val ident: String, val caption: String, val currencies: Map<String, List<String>>)
