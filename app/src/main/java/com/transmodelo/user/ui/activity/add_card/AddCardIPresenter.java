package com.transmodelo.user.ui.activity.add_card;

import com.transmodelo.user.base.MvpPresenter;

/**
 * Created by santhosh@appoets.com on 19-05-2018.
 */
interface AddCardIPresenter<V extends AddCardIView> extends MvpPresenter<V> {
    void card(String cardIssuer,String token,String cardInfo);
}
