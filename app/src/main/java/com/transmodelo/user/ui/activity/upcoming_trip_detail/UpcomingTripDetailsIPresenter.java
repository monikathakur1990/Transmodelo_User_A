package com.transmodelo.user.ui.activity.upcoming_trip_detail;

import com.transmodelo.user.base.MvpPresenter;

public interface UpcomingTripDetailsIPresenter<V extends UpcomingTripDetailsIView> extends MvpPresenter<V> {

    void getUpcomingTripDetails(Integer requestId);
}
