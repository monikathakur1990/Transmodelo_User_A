package com.transmodelo.user.ui.activity.passbook;

import com.transmodelo.user.base.MvpView;
import com.transmodelo.user.data.network.model.WalletResponse;

public interface WalletHistoryIView extends MvpView {
    void onSuccess(WalletResponse response);

    void onError(Throwable e);
}
