package com.transmodelo.user.data.network.util;

import android.os.Build;

import androidx.annotation.RequiresApi;
import com.transmodelo.user.data.network.model.PagoPluxCardModel;


import java.util.Base64;

public class UrlEncodeUtil {

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String formatParamByEnvironment(PagoPluxCardModel pagoPluxCardModel) {
        Base64.Encoder encoder = Base64.getEncoder();
        String preResult = ParamRequest.ADMIN_ENVIRONMENT.value() + "=" + encoder.encodeToString(pagoPluxCardModel.getPayboxEnvironment().getBytes()) + "&" +
                ParamRequest.ADMIN_DISPLAY.value() + "=" + encoder.encodeToString(pagoPluxCardModel.getPayboxDisplay().getBytes()) + "&" +
                ParamRequest.ADMIN_LANGUAGE.value() + "=" + encoder.encodeToString(pagoPluxCardModel.getPayboxLanguage().getBytes()) + "&" +
                ParamRequest.ADMIN_PRODUCTION.value() + "=" + encoder.encodeToString(pagoPluxCardModel.getPayboxProduction().toString().getBytes()) + "&" +
                ParamRequest.ADMIN_ESTABLISHMENT.value() + "=" + encoder.encodeToString(pagoPluxCardModel.getPayboxRename().getBytes()) + "&" +
                ParamRequest.CLIENT_NAME.value() + "=" + encoder.encodeToString(pagoPluxCardModel.getPayboxSendname().getBytes()) + "&" +
                ParamRequest.CLIENT_EMAIL.value() + "=" + encoder.encodeToString(pagoPluxCardModel.getPayboxSendmail().getBytes()) + "&" +
                ParamRequest.CLIENT_ADDRESS.value() + "=" + encoder.encodeToString(pagoPluxCardModel.getPayboxDirection().getBytes()) + "&" +
                ParamRequest.CLIENT_PLAN.value() + "=" + encoder.encodeToString(pagoPluxCardModel.getPayboxIdPlan().getBytes()) + "&" +
                ParamRequest.CLIENT_IDENTIFICATION.value() + "=" + encoder.encodeToString(pagoPluxCardModel.getPayBoxClientIdentification().getBytes()) + "&" +
                ParamRequest.CLIENT_REMAIL.value() + "=" + encoder.encodeToString(pagoPluxCardModel.getPayboxRemail().getBytes()) + "&" +
                ParamRequest.CLIENT_PHONE.value() + "=" + encoder.encodeToString(pagoPluxCardModel.getPayBoxClientPhone().getBytes());
        String BASE_URL = pagoPluxCardModel.getPayboxEnvironment().equals("produccion") ? AppConstant.BASE_URL_PRODUCTION : AppConstant.BASE_URL_SANDBOX;
        return BASE_URL.concat("?").concat(encoder.encodeToString(preResult.getBytes()));
    }
}
