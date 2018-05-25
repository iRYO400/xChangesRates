package workshop.akbolatss.xchangesrates.repositories

import io.reactivex.Single
import workshop.akbolatss.xchangesrates.model.dao.GlobalNotification

/**
 * Author: Akbolat Sadvakassov
 * Date: 20.01.2018
 */

internal interface NotificationRepository {

    val allNotifications: Single<List<GlobalNotification>>

    fun initDefault(): Single<Boolean>
}
