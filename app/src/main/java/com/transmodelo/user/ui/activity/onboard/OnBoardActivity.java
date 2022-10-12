package com.transmodelo.user.ui.activity.onboard;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.IntentSender;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.Task;
import com.transmodelo.user.BuildConfig;
import com.transmodelo.user.MvpApplication;
import com.transmodelo.user.R;
import com.transmodelo.user.base.BaseActivity;
import com.transmodelo.user.data.SharedHelper;
import com.transmodelo.user.data.network.model.CheckVersion;
import com.transmodelo.user.data.network.model.Service;
import com.transmodelo.user.data.network.model.User;
import com.transmodelo.user.ui.activity.login.EmailActivity;
import com.transmodelo.user.ui.activity.main.MainActivity;
import com.transmodelo.user.ui.activity.register.RegisterActivity;
import com.transmodelo.user.data.network.model.WalkThrough;
import com.transmodelo.user.ui.activity.splash.SplashActivity;
import com.transmodelo.user.ui.activity.splash.SplashIView;
import com.transmodelo.user.ui.activity.splash.SplashPresenter;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.transmodelo.user.data.SharedHelper.putKey;

public class OnBoardActivity extends BaseActivity implements ViewPager.OnPageChangeListener, OnBoardIView, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    @BindView(R.id.view_pager)
    ViewPager viewPager;
    @BindView(R.id.layoutDots)
    LinearLayout layoutDots;
    @BindView(R.id.llLoginContainer)
    LinearLayout layoutLoginRegister;
    private MyViewPagerAdapter adapter;
    private int dotsCount;
    private ImageView[] dots;
    private OnBoardPresenter<OnBoardActivity> presenter = new OnBoardPresenter<>();
    private GoogleApiClient mGoogleApiClient;

    @Override
    public int getLayoutId() {
        return R.layout.activity_on_board;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);

//        List<WalkThrough> list = new ArrayList<>();
//        list.add(new WalkThrough(R.drawable.bg_walk_one,
//                getString(R.string.walk_1), getString(R.string.walk_1_description)));
//        list.add(new WalkThrough(R.drawable.bg_walk_two,
//                getString(R.string.walk_2), getString(R.string.walk_2_description)));
//        list.add(new WalkThrough(R.drawable.bg_walk_three,
//                getString(R.string.walk_3), getString(R.string.walk_3_description)));
//
//        adapter = new MyViewPagerAdapter(this, list);
//        viewPager.setAdapter(adapter);
//        viewPager.setCurrentItem(0);
//        viewPager.addOnPageChangeListener(this);
//        addBottomDots();

        presenter.attachView(this);

        printHashKey();
        //  note.setText(getString(R.string.version,
        //       String.valueOf(BuildConfig.VERSION_CODE)));

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();
        mGoogleApiClient.connect();

        try {
            final MessageDigest md = MessageDigest.getInstance("SHA");
            if (Build.VERSION.SDK_INT >= 28) {

                PackageInfo packageInfo = getPackageManager().getPackageInfo
                        (getPackageName(), PackageManager.GET_SIGNING_CERTIFICATES);
                Signature[] signatures = packageInfo.signingInfo.getApkContentsSigners();

                for (Signature signature : signatures) {
                    md.update(signature.toByteArray());
                    Log.d("KeyHash:", new String(Base64.encode(md.digest(), Base64.DEFAULT)));
                }

            } else {
                PackageInfo info = getPackageManager().getPackageInfo
                        (BuildConfig.APPLICATION_ID, PackageManager.GET_SIGNATURES);
                for (Signature signature : info.signatures) {
                    md.update(signature.toByteArray());
                    Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
                }
            }
        } catch (PackageManager.NameNotFoundException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        Log.d("FCM", "FCM Token: " + SharedHelper.getKey(baseActivity(), "device_token"));
        layoutLoginRegister.setVisibility(View.GONE);

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        for (int i = 0; i < dotsCount; i++)
            dots[i].setImageDrawable(getResources().getDrawable(R.drawable.ic_dot_unselected));
        dots[position].setImageDrawable(getResources().getDrawable(R.drawable.ic_dot_selected));
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public void printHashKey() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.plusone.partner",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        }
        catch (PackageManager.NameNotFoundException e) {
        }
        catch (NoSuchAlgorithmException e) {
        }
    }

    private void addBottomDots() {
        dotsCount = adapter.getCount();
        dots = new ImageView[dotsCount];
        if (dotsCount == 0)
            return;

        layoutDots.removeAllViews();

        for (int i = 0; i < dotsCount; i++) {
            dots[i] = new ImageView(this);
            dots[i].setImageDrawable(getResources().getDrawable(R.drawable.ic_dot_unselected));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

            params.setMargins(4, 4, 4, 4);

            layoutDots.addView(dots[i], params);
        }

        dots[0].setImageDrawable(getResources().getDrawable(R.drawable.ic_dot_selected));
    }

    @OnClick({R.id.sign_in, R.id.sign_up})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.sign_in:
                startActivity(new Intent(this, EmailActivity.class));
                break;
            case R.id.sign_up:
                startActivity(new Intent(this, RegisterActivity.class));
                break;
          /*  case R.id.social_login:
                startActivity(new Intent(this, SocialLoginActivity.class));
                break;*/
        }
    }

    @Override
    public void onSuccess(List<Service> services) {
        AsyncTask.execute(() -> {
            try {
                for (Service service : services)
                    if (!TextUtils.isEmpty(service.getMarker())) {
                        Bitmap b = getBitmapFromURL(service.getMarker());
                        if (b != null)
                            putKey(this, service.getName() + service.getId(), encodeBase64(b));
                    }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
    @Override
    public void onError(Throwable e) {
        layoutLoginRegister.setVisibility(View.VISIBLE);
        handleError(e);
    }
    @Override
    public void onSuccess(User user) {
        Log.e( "onSuccess: ",user.toString() );
        putKey(this, "stripe_publishable_key", user.getStripePublishableKey());
        putKey(this, "user_id", String.valueOf(user.getId()));
        putKey(this, "appContact", user.getAppContact());
        putKey(this, "currency", user.getCurrency());
        putKey(this, "lang", user.getLanguage());
        putKey(this, "walletBalance", String.valueOf(user.getWalletBalance()));
        putKey(this, "logged_in", true);
        putKey(this, "measurementType", user.getMeasurement());
        putKey(this, "referral_code", user.getReferral_unique_id());
        putKey(this, "referral_count", user.getReferral_count());
        MvpApplication.showOTP = (user.getRide_otp() != null) && (user.getRide_otp().equals("1"));
        startActivity(new Intent(OnBoardActivity.this, MainActivity.class));
        finish();
    }

    @Override
    public void onSuccess(CheckVersion version) {

        try {
            if (!version.getForceUpdate()) new Handler().postDelayed(() -> {
                Log.d("Loggedin", String.valueOf(SharedHelper.getBoolKey(OnBoardActivity.this, "logged_in", false)));
                String device_token = String.valueOf(SharedHelper.getKey(OnBoardActivity.this, "device_token"));
                Log.d("device_token", device_token);
                checkUserAppInstalled();
            }, 0);
            else showAlertDialog(version.getUrl());

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void showAlertDialog(String url) {
        AlertDialog.Builder builder = new AlertDialog.Builder(OnBoardActivity.this);
        builder.setTitle(getString(R.string.new_version_update));
        builder.setMessage(getString(R.string.update_version_message));
        builder.setPositiveButton(getString(R.string.update), (dialog, which) -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TOP
                    | Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
        });
        builder.show();
    }

    private void checkUserAppInstalled() {
        if (isPackageExisted(OnBoardActivity.this))
            showWarningAlert(getString(R.string.user_provider_app_warning));
        else redirectionToHome();
    }

    private boolean isPackageExisted(Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo(BuildConfig.DRIVER_PACKAGE, PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
        return true;
    }

    private void showWarningAlert(String message) {
        try {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(OnBoardActivity.this);
            alertDialogBuilder
                    .setTitle(getResources().getString(R.string.warning))
                    .setMessage(message)
                    .setCancelable(false)
                    .setPositiveButton(getResources().getString(R.string.continue_app),
                            (dialog, id) -> redirectionToHome());
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void redirectionToHome() {
        Log.e("redirectionToHome: ","here" );
        if (SharedHelper.getBoolKey(OnBoardActivity.this, "logged_in", false))
            presenter.profile();
        else {
            layoutLoginRegister.setVisibility(View.VISIBLE);
//            Intent nextScreen = new Intent(OnBoardActivity.this, OnBoardActivity.class);
//            nextScreen.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//            startActivity(nextScreen);
//            finishAffinity();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000 * 10);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        builder.setAlwaysShow(true);

        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
        task.addOnSuccessListener(this, locationSettingsResponse -> runOnUiThread(this::checkVersion));

        task.addOnFailureListener(this, e -> {
            if (e instanceof ResolvableApiException) try {
                ResolvableApiException resolvable = (ResolvableApiException) e;
                resolvable.startResolutionForResult(OnBoardActivity.this, REQUEST_CHECK_SETTINGS);
            } catch (IntentSender.SendIntentException sendEx) {
                Toast.makeText(this, sendEx.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkVersion() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("version", BuildConfig.VERSION_CODE);
        map.put("device_type", BuildConfig.DEVICE_TYPE);
        map.put("sender", "provider");
        presenter.checkVersion(map);
        if (!TextUtils.isEmpty(SharedHelper.getKey(MvpApplication.getInstance(), "access_token", null)))
            presenter.services();
    }
    @Override
    public void onConnectionSuspended(int i) {
        if (mGoogleApiClient != null && !mGoogleApiClient.isConnected()) mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (mGoogleApiClient != null && !mGoogleApiClient.isConnected())
            mGoogleApiClient.reconnect();
    }

    public class MyViewPagerAdapter extends PagerAdapter {
        List<WalkThrough> list;
        Context context;

        MyViewPagerAdapter(Context context, List<WalkThrough> list) {
            this.list = list;
            this.context = context;

        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            View itemView = LayoutInflater.from(container.getContext()).inflate(R.layout.pager_item, container, false);
            WalkThrough walk = list.get(position);

            TextView title = itemView.findViewById(R.id.title);
            TextView description = itemView.findViewById(R.id.description);
            ImageView imageView = itemView.findViewById(R.id.img_pager_item);

            title.setText(walk.title);
            description.setText(walk.description);
            Glide.with(context).load(walk.drawable).into(imageView);
            container.addView(itemView);

            return itemView;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object obj) {
            return view == obj;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }
    @Override
    protected void onDestroy() {
        presenter.onDetach();
        super.onDestroy();
    }
}
