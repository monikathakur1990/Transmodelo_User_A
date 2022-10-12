package com.transmodelo.user.ui.activity.invite_friend;

import com.transmodelo.user.base.MvpView;
import com.transmodelo.user.data.network.model.User;

public interface InviteFriendIView extends MvpView {

    void onSuccess(User user);

    void onError(Throwable e);

}
