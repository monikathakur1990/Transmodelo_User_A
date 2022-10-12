package com.transmodelo.user.ui.activity.coupon;

import com.transmodelo.user.base.MvpPresenter;

public interface CouponIPresenter<V extends CouponIView> extends MvpPresenter<V> {
    void coupon();
}
