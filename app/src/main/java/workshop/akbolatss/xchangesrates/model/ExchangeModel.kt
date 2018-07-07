package workshop.akbolatss.xchangesrates.model


data class ExchangeModel(val ident: String, val caption: String, val currencies: Map<String, List<String>>)
