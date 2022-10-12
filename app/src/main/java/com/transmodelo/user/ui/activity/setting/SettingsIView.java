package com.transmodelo.user.ui.activity.setting;

import com.transmodelo.user.base.MvpView;
import com.transmodelo.user.data.network.model.AddressResponse;

public interface SettingsIView extends MvpView {

    void onSuccessAddress(Object object);

    void onLanguageChanged(Object object);

    void onSuccess(AddressResponse address);

    void onError(Throwable e);
}
