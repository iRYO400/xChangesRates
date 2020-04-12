package workshop.akbolatss.xchangesrates.domain.repository

import workshop.akbolatss.xchangesrates.base.resource.Either
import workshop.akbolatss.xchangesrates.base.resource.Failure
import workshop.akbolatss.xchangesrates.domain.model.Chart

/**
 * Author: Akbolat Sadvakassov
 * Date: 04.01.2018
 */

interface ChartRepository {

    suspend fun download(
        exchange: String,
        coin: String,
        currency: String,
        timing: String
    ): Either<Failure, Chart>
}
