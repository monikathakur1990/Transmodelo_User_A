package com.transmodelo.user.ui.fragment.searching;

import com.transmodelo.user.base.MvpView;

public interface SearchingIView extends MvpView {
    void onSuccess(Object object);

    void onError(Throwable e);
}
