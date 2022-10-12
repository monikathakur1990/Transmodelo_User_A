package com.transmodelo.user.ui.activity.setting;

import com.transmodelo.user.base.MvpPresenter;

import java.util.HashMap;

public interface SettingsIPresenter<V extends SettingsIView> extends MvpPresenter<V> {
    void addAddress(HashMap<String, Object> params);

    void deleteAddress(Integer id, HashMap<String, Object> params);

    void address();

    void changeLanguage(String languageID);
}
