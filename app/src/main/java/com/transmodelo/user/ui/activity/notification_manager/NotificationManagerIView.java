package com.transmodelo.user.ui.activity.notification_manager;

import com.transmodelo.user.base.MvpView;
import com.transmodelo.user.data.network.model.NotificationManager;

import java.util.List;

public interface NotificationManagerIView extends MvpView {

    void onSuccess(List<NotificationManager> notificationManager);

    void onError(Throwable e);

}