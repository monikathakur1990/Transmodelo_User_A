package com.transmodelo.user.ui.activity.notification_manager;

import com.transmodelo.user.base.MvpPresenter;

public interface NotificationManagerIPresenter<V extends NotificationManagerIView> extends MvpPresenter<V> {
    void getNotificationManager();
}
