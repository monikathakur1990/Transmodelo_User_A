package com.transmodelo.user.ui.activity.add_card;

import com.transmodelo.user.base.BasePresenter;
import com.transmodelo.user.data.network.APIClient;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


public class AddCardPresenter<V extends AddCardIView> extends BasePresenter<V> implements AddCardIPresenter<V> {
    @Override
    public void card(String cardIssuer,String token,String cardInfo) {

        getCompositeDisposable().add(APIClient.getAPIClient().card(cardIssuer,token,cardInfo)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object -> getMvpView().onSuccess(object),
                        throwable -> getMvpView().onError(throwable)));
    }
}
