package com.transmodelo.user.ui.activity.past_trip_detail;

import com.transmodelo.user.base.MvpView;
import com.transmodelo.user.data.network.model.Datum;

import java.util.List;

public interface PastTripDetailsIView extends MvpView {

    void onSuccess(List<Datum> pastTripDetails);

    void onError(Throwable e);
}
