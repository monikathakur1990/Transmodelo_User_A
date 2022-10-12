package com.transmodelo.user.ui.activity.register;

import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.transmodelo.user.R;
import com.transmodelo.user.base.BaseActivity;
import com.transmodelo.user.data.SharedHelper;
import com.transmodelo.user.data.network.model.RegisterResponse;
import com.transmodelo.user.data.network.model.SettingsResponse;
import com.transmodelo.user.ui.activity.main.MainActivity;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;

public class VerifyPhoneActivity extends BaseActivity implements RegisterIView {
    private EditText editTextOtp;
    private String mVerificationId;
    private FirebaseAuth mAuth;
    private HashMap<String, Object> map = new HashMap<>();
    private String mobile;
    private String isoCode;
    private RegisterPresenter presenter;

    @Override
    public int getLayoutId() {
        return R.layout.activity_verify_phone;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        mAuth = FirebaseAuth.getInstance();
        editTextOtp = findViewById(R.id.txtOtp);
        presenter = new RegisterPresenter();
        presenter.attachView(this);
        presenter.getSettings();
        Intent intent = getIntent();
        mobile = intent.getStringExtra("mobile");
        isoCode = intent.getStringExtra("ISOCode");

        map.put("first_name", intent.getStringExtra("first_name"));
        map.put("last_name", intent.getStringExtra("last_name"));
        map.put("email", intent.getStringExtra("email"));
        map.put("mobile", intent.getStringExtra("mobile"));
        map.put("country_code", intent.getStringExtra("countryCode"));
        map.put("password", intent.getStringExtra("password"));

        /*
         * hidden because client don't want this field
         * map.put("ci", intent.getStringExtra("ci")); */

        map.put("password_confirmation", intent.getStringExtra("password_confirmation"));
        map.put("device_token", intent.getStringExtra("device_token"));
        map.put("device_id", intent.getStringExtra("device_id"));
        map.put("login_by", intent.getStringExtra("login_by"));
        map.put("device_type", intent.getStringExtra("device_type"));

        sendVerificationCode(mobile, isoCode);
        findViewById(R.id.next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String code = editTextOtp.getText().toString().trim();
                if (code.isEmpty() || code.length() < 6) {
                    editTextOtp.setError("Enter valid code");
                    editTextOtp.requestFocus();
                    return;
                }


                //verifying the code entered manually
                verifyVerificationCode(code);


//                SharedHelper.putKey(VerifyPhoneActivity.this, "countryCode", "+" + isoCode);
//                SharedHelper.putKey(VerifyPhoneActivity.this, "mobile", mobile);
//                register(map);

            }
        });
    }

    private void sendVerificationCode(String mobile, String isoCode) {

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                isoCode + "" + mobile,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallbacks);
    }

    //    @OnClick({R.id.back})
//    public void onViewClicked(View view) {
//        switch (view.getId()) {
//
//            case R.id.back:
//                onBackPressed();
//                break;
//        }
//    }

    private void verifyVerificationCode(String code) {
        //creating the credential
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);

        //signing the user
        signInWithPhoneAuthCredential(credential);
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

            //Getting the code sent by SMS
            String code = phoneAuthCredential.getSmsCode();

            //sometime the code is not detected automatically
            //in this case the code will be null
            //so user has to manually enter the code
            if (code != null) {
                editTextOtp.setText(code);
                //verifying the code
                verifyVerificationCode(code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(VerifyPhoneActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);

            //storing the verification id that is sent to the user
            mVerificationId = s;
        }
    };

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(VerifyPhoneActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //verification successful we will start the profile activity
                            Toasty.success(VerifyPhoneActivity.this, "Verificación exitosa", Toast.LENGTH_SHORT, true).show();
                            //Intent intent = new Intent(VerifyPhoneActivity.this, RegisterActivity.class);
                            // startActivity(intent);
                            SharedHelper.putKey(VerifyPhoneActivity.this, "countryCode", "+" + isoCode);
                            SharedHelper.putKey(VerifyPhoneActivity.this, "mobile", mobile);
                            register(map);

                        } else {

                            //verification unsuccessful.. display an error message

                            String message = "Algo está mal, lo arreglaremos pronto ...";

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                message = "Se ingresó un código no válido ...";
                            }
                            Toasty.error(VerifyPhoneActivity.this, message, Toast.LENGTH_SHORT, true).show();

                        }
                    }
                });
    }

    private void register(HashMap map) {
        showLoading();
        presenter.register(map);
    }

    @Override
    public void onSuccess(RegisterResponse response) {
        try {
            hideLoading();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        Toast.makeText(this, getString(R.string.you_have_been_successfully_registered), Toast.LENGTH_SHORT).show();
        SharedHelper.putKey(this, "access_token", "Bearer " + response.getAccessToken());
        SharedHelper.putKey(this, "logged_in", true);
        finishAffinity();
        startActivity(new Intent(this, MainActivity.class));
    }

    public void onError(Throwable e) {
        hideLoading();
        if (e != null)
            onErrorBase(e);
    }

    @Override
    public void onSuccess(Object verifyEmail) {

    }


    @Override
    public void onSuccess(SettingsResponse response) {
        //  lnrReferralCode.setVisibility(response.getReferral().getReferral().equalsIgnoreCase("1") ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onSuccessPhoneNumber(Object object) {

    }

    @Override
    public void onVerifyPhoneNumberError(Throwable e) {

    }

    @Override
    public void onVerifyEmailError(Throwable e) {

    }
}

