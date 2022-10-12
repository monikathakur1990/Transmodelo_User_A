package com.transmodelo.user.ui.activity.login;

import com.transmodelo.user.base.MvpView;
import com.transmodelo.user.data.network.model.ForgotResponse;
import com.transmodelo.user.data.network.model.Token;

public interface LoginIView extends MvpView {
    void onSuccess(Token token);

    void onSuccess(ForgotResponse object);

    void onError(Throwable e);
}
