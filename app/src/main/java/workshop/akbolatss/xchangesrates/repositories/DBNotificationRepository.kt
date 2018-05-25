package workshop.akbolatss.xchangesrates.repositories

import java.util.concurrent.Callable

import io.reactivex.Single
import workshop.akbolatss.xchangesrates.model.dao.DaoSession
import workshop.akbolatss.xchangesrates.model.dao.GlobalNotification
import workshop.akbolatss.xchangesrates.model.dao.GlobalNotificationDao

/**
 * Author: Akbolat Sadvakassov
 * Date: 20.01.2018
 */

class DBNotificationRepository(internal var mDaoSession: DaoSession) : NotificationRepository {

    override val allNotifications: Single<List<GlobalNotification>>
        get() = Single.fromCallable {
            val notificationDao = mDaoSession.globalNotificationDao
            notificationDao.loadAll()
        }

    fun onSaveChanges(globalNotifications: List<GlobalNotification>): Single<List<GlobalNotification>> {
        return Single.fromCallable {
            val notificationDao = mDaoSession.globalNotificationDao
            notificationDao.deleteAll()

            for (i in globalNotifications.indices) {
                notificationDao.insert(globalNotifications[i])
            }
            globalNotifications
        }
    }

    override fun initDefault(): Single<Boolean> {
        return Single.fromCallable {
            val notificationDao = mDaoSession.globalNotificationDao
            var snapshotNotification = GlobalNotification()
            snapshotNotification.isActive = false
            snapshotNotification.hour = 8
            snapshotNotification.minutes = 0
            snapshotNotification.buildName()
            notificationDao.insert(snapshotNotification)

            snapshotNotification = GlobalNotification()
            snapshotNotification.isActive = false
            snapshotNotification.hour = 13
            snapshotNotification.minutes = 0
            snapshotNotification.buildName()
            notificationDao.insert(snapshotNotification)

            snapshotNotification = GlobalNotification()
            snapshotNotification.isActive = false
            snapshotNotification.hour = 15
            snapshotNotification.minutes = 0
            snapshotNotification.buildName()
            notificationDao.insert(snapshotNotification)

            snapshotNotification = GlobalNotification()
            snapshotNotification.isActive = false
            snapshotNotification.hour = 18
            snapshotNotification.minutes = 0
            snapshotNotification.buildName()
            notificationDao.insert(snapshotNotification)

            snapshotNotification = GlobalNotification()
            snapshotNotification.isActive = false
            snapshotNotification.hour = 21
            snapshotNotification.minutes = 0
            snapshotNotification.buildName()
            notificationDao.insert(snapshotNotification)
            true
        }
    }
}
