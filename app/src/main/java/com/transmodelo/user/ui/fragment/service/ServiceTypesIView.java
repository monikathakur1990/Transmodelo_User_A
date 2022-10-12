package com.transmodelo.user.ui.fragment.service;

import com.transmodelo.user.base.MvpView;
import com.transmodelo.user.data.network.model.Service;

import java.util.List;

public interface ServiceTypesIView extends MvpView {

    void onSuccess(List<Service> serviceList);

    void onError(Throwable e);

    void onSuccess(Object object);
}
