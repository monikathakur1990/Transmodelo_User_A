package com.transmodelo.user.ui.activity.coupon;

import com.transmodelo.user.base.MvpView;
import com.transmodelo.user.data.network.model.PromoResponse;

public interface CouponIView extends MvpView {
    void onSuccess(PromoResponse object);
    void onError(Throwable e);
}
