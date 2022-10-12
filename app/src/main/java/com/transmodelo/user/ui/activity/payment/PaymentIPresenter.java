package com.transmodelo.user.ui.activity.payment;

import com.transmodelo.user.base.MvpPresenter;

/**
 * Created by santhosh@appoets.com on 19-05-2018.
 */
public interface PaymentIPresenter<V extends PaymentIView> extends MvpPresenter<V> {
    void deleteCard(String cardId);
    void card();

    void addCard(String cardIssuer,String token,String cardInfo);

    //    void payuMoneyChecksum(String request_id,String user_address_id,String paymentmode);
    void payuMoneyChecksum();

        void paytmCheckSum(String request_id,String paymentmode);
   void getBrainTreeToken();
}
