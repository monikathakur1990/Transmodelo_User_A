package com.transmodelo.user.ui.fragment.book_ride;

import com.transmodelo.user.base.MvpView;
import com.transmodelo.user.data.network.model.PromoResponse;


public interface BookRideIView extends MvpView {

    void onSuccess(Object object);

    void onError(Throwable e);

    void onSuccessCoupon(PromoResponse promoResponse);
}
