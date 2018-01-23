package workshop.akbolatss.xchangesrates.repositories;

import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import workshop.akbolatss.xchangesrates.model.dao.DaoSession;
import workshop.akbolatss.xchangesrates.model.dao.Notification;
import workshop.akbolatss.xchangesrates.model.dao.NotificationDao;

/**
 * Author: Akbolat Sadvakassov
 * Date: 20.01.2018
 */

public class DBNotificationRepository implements NotificationRepository {

    private DaoSession mDaoSession;

    public DBNotificationRepository(DaoSession mDaoSession) {
        this.mDaoSession = mDaoSession;
    }

    @Override
    public Single<List<Notification>> getAllNotifications() {
        return Single.fromCallable(new Callable<List<Notification>>() {
            @Override
            public List<Notification> call() throws Exception {
                NotificationDao notificationDao = mDaoSession.getNotificationDao();
                return notificationDao.loadAll();
            }
        });
    }

    public Single<List<Notification>> onSaveChanges(final List<Notification> notificationList) {
        return Single.fromCallable(new Callable<List<Notification>>() {
            @Override
            public List<Notification> call() throws Exception {
                NotificationDao notificationDao = mDaoSession.getNotificationDao();
                notificationDao.deleteAll();

                for (int i = 0; i < notificationList.size(); i++){
                    notificationDao.insert(notificationList.get(i));
                }
                return notificationList;
            }
        });
    }

    public Single<Boolean> initDefault() {
        return Single.fromCallable(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                NotificationDao notificationDao = mDaoSession.getNotificationDao();
                Notification notification = new Notification();
                notification.setIsActive(false);
                notification.setName("08:00");
                notification.setHour(8);
                notification.setMinute(0);
                notificationDao.insert(notification);

                notification = new Notification();
                notification.setIsActive(false);
                notification.setName("12:00");
                notification.setHour(13);
                notification.setMinute(0);
                notificationDao.insert(notification);

                notification = new Notification();
                notification.setIsActive(false);
                notification.setName("15:00");
                notification.setHour(15);
                notification.setMinute(0);
                notificationDao.insert(notification);

                notification = new Notification();
                notification.setIsActive(false);
                notification.setName("18:00");
                notification.setHour(18);
                notification.setMinute(0);
                notificationDao.insert(notification);

                notification = new Notification();
                notification.setIsActive(false);
                notification.setName("21:00");
                notification.setHour(21);
                notification.setMinute(0);
                notificationDao.insert(notification);
                return true;
            }
        });
    }
}
