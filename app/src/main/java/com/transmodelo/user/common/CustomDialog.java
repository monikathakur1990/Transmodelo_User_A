package com.transmodelo.user.common;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;

import com.transmodelo.user.R;


public class CustomDialog extends Dialog {

    public CustomDialog(Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_dialog);
        setCancelable(false);
    }
}