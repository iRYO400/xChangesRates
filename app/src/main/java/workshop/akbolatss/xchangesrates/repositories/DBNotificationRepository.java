package workshop.akbolatss.xchangesrates.repositories;

import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Single;
import workshop.akbolatss.xchangesrates.model.dao.DaoSession;
import workshop.akbolatss.xchangesrates.model.dao.GlobalNotification;
import workshop.akbolatss.xchangesrates.model.dao.GlobalNotificationDao;

/**
 * Author: Akbolat Sadvakassov
 * Date: 20.01.2018
 */

public class DBNotificationRepository implements NotificationRepository {

    DaoSession mDaoSession;

    public DBNotificationRepository(DaoSession mDaoSession) {
        this.mDaoSession = mDaoSession;
    }

    @Override
    public Single<List<GlobalNotification>> getAllNotifications() {
        return Single.fromCallable(new Callable<List<GlobalNotification>>() {
            @Override
            public List<GlobalNotification> call() throws Exception {
                GlobalNotificationDao notificationDao = mDaoSession.getGlobalNotificationDao();
                return notificationDao.loadAll();
            }
        });
    }

    public Single<List<GlobalNotification>> onSaveChanges(final List<GlobalNotification> globalNotifications) {
        return Single.fromCallable(new Callable<List<GlobalNotification>>() {
            @Override
            public List<GlobalNotification> call() throws Exception {
                GlobalNotificationDao notificationDao = mDaoSession.getGlobalNotificationDao();
                notificationDao.deleteAll();

                for (int i = 0; i < globalNotifications.size(); i++){
                    notificationDao.insert(globalNotifications.get(i));
                }
                return globalNotifications;
            }
        });
    }

    public Single<Boolean> initDefault() {
        return Single.fromCallable(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                GlobalNotificationDao notificationDao = mDaoSession.getGlobalNotificationDao();
                GlobalNotification snapshotNotification = new GlobalNotification();
                snapshotNotification.setIsActive(false);
                snapshotNotification.setHour(8);
                snapshotNotification.setMinutes(0);
                snapshotNotification.buildName();
                notificationDao.insert(snapshotNotification);

                snapshotNotification = new GlobalNotification();
                snapshotNotification.setIsActive(false);
                snapshotNotification.setHour(13);
                snapshotNotification.setMinutes(0);
                snapshotNotification.buildName();
                notificationDao.insert(snapshotNotification);

                snapshotNotification = new GlobalNotification();
                snapshotNotification.setIsActive(false);
                snapshotNotification.setHour(15);
                snapshotNotification.setMinutes(0);
                snapshotNotification.buildName();
                notificationDao.insert(snapshotNotification);

                snapshotNotification = new GlobalNotification();
                snapshotNotification.setIsActive(false);
                snapshotNotification.setHour(18);
                snapshotNotification.setMinutes(0);
                snapshotNotification.buildName();
                notificationDao.insert(snapshotNotification);

                snapshotNotification = new GlobalNotification();
                snapshotNotification.setIsActive(false);
                snapshotNotification.setHour(21);
                snapshotNotification.setMinutes(0);
                snapshotNotification.buildName();
                notificationDao.insert(snapshotNotification);
                return true;
            }
        });
    }
}
