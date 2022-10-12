package com.transmodelo.user.ui.activity.card;

import com.transmodelo.user.base.MvpPresenter;


public interface CarsIPresenter<V extends CardsIView> extends MvpPresenter<V> {
    void card();
}
