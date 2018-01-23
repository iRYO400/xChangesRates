package workshop.akbolatss.xchangesrates.repositories;

import java.util.List;

import io.reactivex.Single;
import workshop.akbolatss.xchangesrates.model.dao.Notification;

/**
 * Author: Akbolat Sadvakassov
 * Date: 20.01.2018
 */

interface NotificationRepository {

    public Single<List<Notification>> getAllNotifications();

    public Single<Boolean> initDefault();
}
