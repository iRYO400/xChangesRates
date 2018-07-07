package workshop.akbolatss.xchangesrates.model.response

/**
 * Author: Akbolat Sadvakassov
 * Date: 05.01.2018
 */

data class ChartResponseData(
        var exchange: String,
        var coin: String,
        var currency: String,
        var source: String,
        var chart: List<ChartResponseDataChart>,
        var info: ChartResponseDataInfo
)