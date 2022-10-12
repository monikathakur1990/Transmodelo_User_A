package com.transmodelo.user.ui.activity.add_card;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.google.gson.Gson;
import com.transmodelo.user.ui.activity.card.CardsActivity;
import com.transmodelo.user.R;
import com.transmodelo.user.base.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

import com.transmodelo.user.data.network.model.PagoPluxResponseModel;

public class AddCardActivity extends BaseActivity implements AddCardIView {
    private static final String TAG = "RegisterCardActivity";

    @BindView(R.id.web_view_card)
    WebView webViewCard;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    private AddCardPresenter<AddCardActivity> presenter = new AddCardPresenter<>();

    @Override
    public int getLayoutId() {
        return R.layout.activity_add_card;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        presenter.attachView(this);
        // Activity title will be updated after the locale has changed in Runtime

        this.initWebView(getIntent().getStringExtra(getString(R.string.intent_extra_key_pagoplux)));

    }
    @SuppressLint({"SetJavaScriptEnabled", "JavascriptInterface"})
    private void initWebView(String urlPagoPlux) {
        webViewCard.setWebViewClient(new WebViewClient());
        this.clearCacheWebView();
        webViewCard.getSettings().setJavaScriptEnabled(true);
        webViewCard.getSettings().setDomStorageEnabled(true);

        webViewCard.addJavascriptInterface(new WebAppInterface(this, webViewCard), getString(R.string.btn_event_js_interface_key));
        webViewCard.loadUrl(urlPagoPlux);

        webViewCard.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progressBar.setVisibility(View.GONE);
                setTitle(view.getTitle());
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                progressBar.setVisibility(View.VISIBLE);
                setTitle(getString(R.string.txt_title_loading_activity_webview));

                webViewCard.loadUrl(getJavaScriptInjection());
            }

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                String msg = (String) error.getDescription();
                Toast.makeText(AddCardActivity.this, "Ocurrio un error: " + msg, Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void clearCacheWebView() {
        webViewCard.clearCache(true);
        webViewCard.clearFormData();
        webViewCard.clearHistory();
        webViewCard.clearSslPreferences();
    }
    private String getJavaScriptInjection() {
        return "javascript:(function(){\n" +
                "window.addEventListener(\"message\", function (event){\n" +
                "if(event.data.hasOwnProperty(\"code\")){\n" +
                "if(event.data.hasOwnProperty(\"detail\") && event.data.hasOwnProperty(\"status\")){\n" +
                "if(event.data.code === 0 && event.data.status === \"succeeded\"){\n" +
                "AndroidAppCallNative.receiveMessage(JSON.stringify(event.data));\n" +
                "}\n" +
                "}\n" +
                "if(event.data.code === 500){\n" +
                "console.log(\"Evento en JS: \" + JSON.stringify(event.data));\n" +
                "}\n" +
                "}\n" +
                "}, false);\n" +
                "})()";
    }

    @Override
    public void onSuccess(Object card) {
        try {
            hideLoading();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        Toast.makeText(this, getString(R.string.card_added), Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, CardsActivity.class);
       // intent.putExtra(myActivity.getString(R.string.intent_extra_key_response_pagoplux), (new Gson()).toJson(pagoPluxresponse));
        startActivity(intent);

        finish();
    }

    @Override
    public void onError(Throwable e) {
        handleError(e);
    }

    @Override
    protected void onDestroy() {
        presenter.onDetach();
        super.onDestroy();
    }

    public class WebAppInterface {
        private final Activity myActivity;
        private final WebView wViewPago;

        WebAppInterface(Activity actvty, WebView webView) {
            myActivity = actvty;
            wViewPago = webView;
        }

        @JavascriptInterface
        public void receiveMessage(String jsonPagoPluxresponse) {
            showLoading();
            Log.d(TAG, "Se capturo evento de JavaScript de la web PagoPlux: " + jsonPagoPluxresponse);
            final PagoPluxResponseModel pagoPluxresponse = new Gson().fromJson(jsonPagoPluxresponse, PagoPluxResponseModel.class);
            Log.d("TAG", "Objeto PagoPluxresponse: " + pagoPluxresponse);
            if (pagoPluxresponse.getStatus().equals("succeeded")) {
                presenter.card(pagoPluxresponse.getDetail().getCardIssuer(), pagoPluxresponse.getDetail().getToken(), pagoPluxresponse.getDetail().getCardInfo());
               // Toast.makeText(myActivity, "Registrado correctamente", Toast.LENGTH_LONG).show();
            }
            else{
                hideLoading();
                Toast.makeText(myActivity, "Registrado Error", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(myActivity, CardsActivity.class);
                intent.putExtra(myActivity.getString(R.string.intent_extra_key_response_pagoplux), (new Gson()).toJson(pagoPluxresponse));
                myActivity.startActivity(intent);
                myActivity.finish();

            }

        }
    }
}