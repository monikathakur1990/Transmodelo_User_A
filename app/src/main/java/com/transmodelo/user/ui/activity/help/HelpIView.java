package com.transmodelo.user.ui.activity.help;

import com.transmodelo.user.base.MvpView;
import com.transmodelo.user.data.network.model.Help;

public interface HelpIView extends MvpView {

    void onSuccess(Help help);

    void onError(Throwable e);
}
