package com.transmodelo.user.ui.activity.wallet;

import com.appoets.paytmpayment.PaytmObject;
import com.transmodelo.user.base.MvpView;
import com.transmodelo.user.data.network.model.AddWallet;
import com.transmodelo.user.data.network.model.BrainTreeResponse;

public interface WalletIView extends MvpView {
    void onSuccess(AddWallet object);

    void onSuccess(PaytmObject object);

    void onSuccess(BrainTreeResponse response);
    void onError(Throwable e);
}
