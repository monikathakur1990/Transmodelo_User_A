package com.transmodelo.user.ui.activity.card;

import android.app.Activity;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.transmodelo.user.R;
import com.transmodelo.user.base.BaseActivity;
import com.transmodelo.user.data.SharedHelper;
import com.transmodelo.user.data.network.model.Card;
import com.transmodelo.user.data.network.model.User;
import com.transmodelo.user.data.network.util.UrlEncodeUtil;
import com.transmodelo.user.ui.activity.add_card.AddCardActivity;
import com.transmodelo.user.data.network.model.PagoPluxCardModel;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.transmodelo.user.common.Constants.RIDE_REQUEST.PAYMENT_MODE;

public class CardsActivity extends BaseActivity implements CardsIView {

    List<Card> cardsList = new ArrayList<>();
    @BindView(R.id.cards_rv)
    RecyclerView cardsRv;
    @BindView(R.id.add_card)
    Button addCard;
    @BindView(R.id.text_view_message)
    TextView text_view_message;
    private CardsPresenter<CardsActivity> presenter = new CardsPresenter<>();

    @Override
    public int getLayoutId() {
        return R.layout.activity_cards;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        presenter.attachView(this);
        // Activity title will be updated after the locale has changed in Runtime
        setTitle(getString(R.string.select_card));

        CardAdapter adapter = new CardAdapter(cardsList);
        cardsRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        cardsRv.setAdapter(adapter);

    }

    @OnClick({R.id.paypal})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.paypal:
                Intent intent = new Intent();
                intent.putExtra(PAYMENT_MODE, "PAYPAL");
                setResult(AppCompatActivity.RESULT_OK, intent);
                finish();
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.card();
    }

    @Override
    public void onSuccess(List<Card> cards) {
        cardsList.clear();
        cardsList.addAll(cards);
        cardsRv.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void onError(Throwable e) {
        handleError(e);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @OnClick(R.id.add_card)
    public void onViewClicked() {
        User user = new Gson().fromJson(SharedHelper.getKey(this, "userInfo"), User.class);

        startActivity(new Intent(this, AddCardActivity.class));
        Intent intent = new Intent(CardsActivity.this, AddCardActivity.class);
        PagoPluxCardModel pagoPluxCardModel = new PagoPluxCardModel("kintoec@gmail.com",
                user.getEmail(),
                "Kinto",
                user.getFirstName()+" "+user.getLastName(),
                true,
                "es",
                "Client Direction",
                user.getMobile(), //cell client
                "0104087770", //required //Client identification
                "TRAYECTOS", // It must be created from the pagoplux dashboard  TRAYECTOS
                "produccion",
                "0");// / 0 card registration, 1 card list

        intent.putExtra(getString(R.string.intent_extra_key_pagoplux), UrlEncodeUtil.formatParamByEnvironment(pagoPluxCardModel));
        startActivity(intent);
        text_view_message.setText(getIntent().getStringExtra(getString(R.string.intent_extra_key_response_pagoplux)) == null ? "" : getIntent().getStringExtra(getString(R.string.intent_extra_key_response_pagoplux)));

}

    @Override
    protected void onDestroy() {
        presenter.onDetach();
        super.onDestroy();
    }

    private class CardAdapter extends RecyclerView.Adapter<CardAdapter.MyViewHolder> {

        private List<Card> list;

        CardAdapter(List<Card> list) {
            this.list = list;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_card, parent, false);

            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            Card item = list.get(position);
            holder.card.setText(getString(R.string.card_ , item.getLastFour()));
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            private RelativeLayout itemView;
            private TextView card;

            MyViewHolder(View view) {
                super(view);
                itemView = view.findViewById(R.id.item_view);
                card = view.findViewById(R.id.card);
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
                int position = getAdapterPosition();
                Card card = list.get(position);
                if (view.getId() == R.id.item_view) {
                    Intent intent = new Intent();
                    intent.putExtra("payment_mode", "CARD");
                    intent.putExtra("card_id", card.getCardId());
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }

            }
        }
    }

}
