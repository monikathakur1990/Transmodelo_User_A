package com.transmodelo.user.ui.fragment.dispute;

import com.transmodelo.user.base.MvpView;
import com.transmodelo.user.data.network.model.DisputeResponse;
import com.transmodelo.user.data.network.model.Help;

import java.util.List;

public interface DisputeIView extends MvpView {

    void onSuccess(Object object);

    void onSuccessDispute(List<DisputeResponse> responseList);

    void onError(Throwable e);

    void onSuccess(Help help);
}
