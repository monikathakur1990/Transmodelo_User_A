package com.transmodelo.user.ui.activity.onboard;

import com.transmodelo.user.base.MvpPresenter;

import java.util.HashMap;

public interface OnBoardIPresenter<V extends OnBoardIView> extends MvpPresenter<V> {

    void services();

    void profile();

    void checkVersion(HashMap<String, Object> map);
}
