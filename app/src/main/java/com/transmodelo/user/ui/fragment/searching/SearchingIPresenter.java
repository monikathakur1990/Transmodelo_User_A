package com.transmodelo.user.ui.fragment.searching;

import com.transmodelo.user.base.MvpPresenter;

import java.util.HashMap;

import retrofit2.http.FieldMap;

/**
 * Created by santhosh@appoets.com on 19-05-2018.
 */
public interface SearchingIPresenter<V extends SearchingIView> extends MvpPresenter<V> {
    void cancelRequest(@FieldMap HashMap<String, Object> params);
}
