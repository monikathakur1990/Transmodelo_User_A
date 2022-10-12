package com.transmodelo.user.ui.activity.upcoming_trip_detail;

import com.transmodelo.user.base.MvpView;
import com.transmodelo.user.data.network.model.Datum;

import java.util.List;

public interface UpcomingTripDetailsIView extends MvpView {

    void onSuccess(List<Datum> upcomingTripDetails);

    void onError(Throwable e);
}
