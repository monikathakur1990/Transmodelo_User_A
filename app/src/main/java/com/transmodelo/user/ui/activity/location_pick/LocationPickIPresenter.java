package com.transmodelo.user.ui.activity.location_pick;

import com.transmodelo.user.base.MvpPresenter;

public interface LocationPickIPresenter<V extends LocationPickIView> extends MvpPresenter<V> {
    void address();
}
