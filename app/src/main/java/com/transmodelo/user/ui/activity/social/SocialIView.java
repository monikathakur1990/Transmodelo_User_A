package com.transmodelo.user.ui.activity.social;

import com.transmodelo.user.base.MvpView;
import com.transmodelo.user.data.network.model.Token;

/**
 * Created by santhosh@appoets.com on 19-05-2018.
 */
public interface SocialIView extends MvpView {
    void onSuccess(Token token);

    void onError(Throwable e);
}
