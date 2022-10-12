package com.transmodelo.user.ui.activity.passbook;

import com.transmodelo.user.base.MvpPresenter;

public interface WalletHistoryIPresenter<V extends WalletHistoryIView> extends MvpPresenter<V> {
    void wallet();
}
