package com.transmodelo.user.ui.activity.location_pick;

import static com.transmodelo.user.MvpApplication.RIDE_REQUEST;
import static com.transmodelo.user.common.Constants.RIDE_REQUEST.DEST_ADD;
import static com.transmodelo.user.common.Constants.RIDE_REQUEST.DEST_LAT;
import static com.transmodelo.user.common.Constants.RIDE_REQUEST.DEST_LONG;
import static com.transmodelo.user.common.Constants.RIDE_REQUEST.SRC_ADD;
import static com.transmodelo.user.common.Constants.RIDE_REQUEST.SRC_LAT;
import static com.transmodelo.user.common.Constants.RIDE_REQUEST.SRC_LONG;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.transmodelo.user.R;
import com.transmodelo.user.base.BaseActivity;
import com.transmodelo.user.common.Constants;
import com.transmodelo.user.common.RecyclerItemClickListener;
import com.transmodelo.user.data.network.model.AddressResponse;
import com.transmodelo.user.data.network.model.UserAddress;
import com.transmodelo.user.ui.adapter.PlacesAutoCompleteAdapter;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LocationPickActivity extends BaseActivity
        implements OnMapReadyCallback,
        GoogleMap.OnCameraMoveListener,
        GoogleMap.OnCameraIdleListener,
        LocationPickIView {

    private static final LatLngBounds BOUNDS_INDIA = new LatLngBounds(new LatLng(-0, 0), new LatLng(0, 0));
    private Location mLastKnownLocation;
    protected PlacesClient mGoogleApiClient;

    @BindView(R.id.appbar)
    AppBarLayout appbar;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.source)
    EditText source;
    @BindView(R.id.destination)
    EditText destination;
    @BindView(R.id.destination_layout)
    LinearLayout destinationLayout;
    @BindView(R.id.home_address_layout)
    LinearLayout homeAddressLayout;
    @BindView(R.id.work_address_layout)
    LinearLayout workAddressLayout;
    @BindView(R.id.home_address)
    TextView homeAddress;
    @BindView(R.id.work_address)
    TextView workAddress;
    @BindView(R.id.locations_rv)
    RecyclerView locationsRv;
    @BindView(R.id.location_bs_layout)
    CardView locationBsLayout;
    @BindView(R.id.dd)
    CoordinatorLayout dd;
    boolean isEnableIdle = false;
    @BindView(R.id.llSource)
    LinearLayout llSource;
    @BindView(R.id.button2)
    Button submit_button;

    private boolean isLocationRvClick = false;
    private boolean isSettingLocationClick = false;
    private boolean mLocationPermissionGranted;
    private GoogleMap mGoogleMap;

    private String s_address;
    private Double s_latitude;
    private Double s_longitude;

    private FusedLocationProviderClient mFusedLocationProviderClient;
    private BottomSheetBehavior mBottomSheetBehavior;
    private Boolean isEditable = true;
    private UserAddress home, work = null;
    private LocationPickPresenter<LocationPickActivity> presenter = new LocationPickPresenter<>();
    private EditText selectedEditText;
    private PlacesAutoCompleteAdapter mAutoCompleteAdapter;
    //Base on action we are show/hide view and setResults
    private String actionName = Constants.LocationActions.SELECT_SOURCE;

    private TextWatcher filterTextWatcher = new TextWatcher() {

        public void afterTextChanged(Editable s) {
            if (isEditable) if (!s.toString().equals("")) {
                locationsRv.setVisibility(View.VISIBLE);
                mAutoCompleteAdapter.getFilter().filter(s.toString());
                if (mBottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED)
                    mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
            if (s.toString().equals("")) locationsRv.setVisibility(View.GONE);
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

    };

    @Override
    public int getLayoutId() {
        return R.layout.activity_location_pick;
    }

    @Override
    public void initView() {
        buildGoogleApiClient();
        ButterKnife.bind(this);
        presenter.attachView(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mBottomSheetBehavior = BottomSheetBehavior.from(locationBsLayout);
        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        mAutoCompleteAdapter = new PlacesAutoCompleteAdapter(this, R.layout.list_item_location, mGoogleApiClient, BOUNDS_INDIA);

        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        locationsRv.setLayoutManager(mLinearLayoutManager);
        locationsRv.setAdapter(mAutoCompleteAdapter);

        source.addTextChangedListener(filterTextWatcher);
        destination.addTextChangedListener(filterTextWatcher);

        source.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) selectedEditText = source;
        });

        destination.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) selectedEditText = destination;
        });

        destination.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                setResult(Activity.RESULT_OK, new Intent());
                finish();
                return true;
            }
            return false;
        });


        source.setText(RIDE_REQUEST.containsKey(SRC_ADD)
                ? TextUtils.isEmpty(Objects.requireNonNull(RIDE_REQUEST.get(SRC_ADD)).toString())
                ? ""
                : String.valueOf(RIDE_REQUEST.get(SRC_ADD))
                : "");

        destination.setText(RIDE_REQUEST.containsKey(DEST_ADD)
                ? TextUtils.isEmpty(Objects.requireNonNull(RIDE_REQUEST.get(DEST_ADD)).toString())
                ? ""
                : String.valueOf(RIDE_REQUEST.get(DEST_ADD))
                : "");


        locationsRv.addOnItemTouchListener(new RecyclerItemClickListener(this, (view, position) -> {

                    if (mAutoCompleteAdapter.getItemCount() == 0) return;

                    final PlacesAutoCompleteAdapter.PlaceAutocomplete item = mAutoCompleteAdapter.getItem(position);
                    final String placeId = String.valueOf(item.placeId);
                    Log.i("LocationPickActivity", "Autocomplete item selected: " + item.address);
                    final List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG);
                    final FetchPlaceRequest request = FetchPlaceRequest.newInstance(placeId, placeFields);

                    mGoogleApiClient.fetchPlace(request).addOnSuccessListener((response) -> {
                        Place place = response.getPlace();
                        LatLng latLng = place.getLatLng();
                        isLocationRvClick = true;
                        isSettingLocationClick = true;

                        setLocationText(String.valueOf(item.address), latLng, isLocationRvClick, isSettingLocationClick);

                        Log.i("LocationPickActivity", "Place found: " + place.getName());

                    }).addOnFailureListener((exception) -> {
                        if (exception instanceof ApiException) {
                            final ApiException apiException = (ApiException) exception;
                            Log.e("LocationPickActivity", "Place not found: " + exception.getMessage());
                            final int statusCode = apiException.getStatusCode();
                            // TODO: Handle error with given status code.
                        }
                    });


                    Log.i("LocationPickActivity", "Clicked: " + item.address);
                    Log.i("LocationPickActivity", "Called getPlaceById to get Place details for " + item.placeId);
                })
        );


        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            actionName = bundle.getString("actionName", Constants.LocationActions.SELECT_SOURCE);

            if (!TextUtils.isEmpty(actionName) && actionName.equalsIgnoreCase(Constants.LocationActions.SELECT_SOURCE)) {
                destination.setCursorVisible(false);
                source.setCursorVisible(true);
                source.requestFocus();
                selectedEditText = source;
            } else if (!TextUtils.isEmpty(actionName) && actionName.equalsIgnoreCase(Constants.LocationActions.SELECT_DESTINATION)) {
                source.setCursorVisible(false);
                destination.setCursorVisible(true);
                destination.setText("");
                destination.requestFocus();
                selectedEditText = destination;
            } else if (!TextUtils.isEmpty(actionName) && actionName.equals(Constants.LocationActions.CHANGE_DESTINATION)) {
                llSource.setVisibility(View.GONE);
                source.setHint(getString(R.string.select_location));
                selectedEditText = destination;
            } else if (!TextUtils.isEmpty(actionName) && (actionName.equals(Constants.LocationActions.SELECT_HOME) || actionName.equals(Constants.LocationActions.SELECT_WORK))) {
                destinationLayout.setVisibility(View.GONE);
                selectedEditText = destination;
                source.setText("");
                source.setHint(getString(R.string.select_location));
            } else {
                destinationLayout.setVisibility(View.VISIBLE);
                llSource.setVisibility(View.VISIBLE);
                source.setHint(getString(R.string.pickup_location));
                selectedEditText = source;
            }

        }


//        presenter.address();
    }

    private void setLocationText(String address, LatLng latLng, boolean isLocationRvClick, boolean isSettingLocationClick) {
        Log.e("LocationPickActivity", "setLocationText: tag: " + selectedEditText.getTag().toString());

        if (address != null && latLng != null) {
            isEditable = false;
            selectedEditText.setText(address);
            isEditable = true;

            if (selectedEditText.getTag().equals("source")) {
                Log.i("LocationPickActivity", "setLocationText: set source");
                s_address = address;
                s_latitude = latLng.latitude;
                s_longitude = latLng.longitude;

                RIDE_REQUEST.put(SRC_ADD, address);
                RIDE_REQUEST.put(SRC_LAT, latLng.latitude);
                RIDE_REQUEST.put(SRC_LONG, latLng.longitude);
            }

            if (selectedEditText.getTag().equals("destination")) {
                Log.i("LocationPickActivity", "setLocationText: set destinantion");

                Log.i("LocationPickActivity", "RIDE_REQUEST: SRC_ADD: " + RIDE_REQUEST.get(SRC_ADD));
                Log.i("LocationPickActivity", "RIDE_REQUEST: SRC_LAT: " + RIDE_REQUEST.get(SRC_LAT));
                Log.i("LocationPickActivity", "RIDE_REQUEST: SRC_LONG: " + RIDE_REQUEST.get(SRC_LONG));

                RIDE_REQUEST.put(DEST_ADD, address);
                RIDE_REQUEST.put(DEST_LAT, latLng.latitude);
                RIDE_REQUEST.put(DEST_LONG, latLng.longitude);

                Log.i("LocationPickActivity", "RIDE_REQUEST: DEST_ADD: " + RIDE_REQUEST.get(DEST_ADD));
                Log.i("LocationPickActivity", "RIDE_REQUEST: DEST_LAT: " + RIDE_REQUEST.get(DEST_LAT));
                Log.i("LocationPickActivity", "RIDE_REQUEST: DEST_LONG: " + RIDE_REQUEST.get(DEST_LONG));

                if (isLocationRvClick) {
                    //  Done functionality called...
                    setResult(AppCompatActivity.RESULT_OK, new Intent());
                    finish();
                }
            }

        } else {

            isEditable = false;
            selectedEditText.setText("");
            locationsRv.setVisibility(View.GONE);
            isEditable = true;

            if (selectedEditText.getTag().equals("source")) {
                Log.i("LocationPickActivity", "setLocationText: else set source");

                RIDE_REQUEST.remove(SRC_ADD);
                RIDE_REQUEST.remove(SRC_LAT);
                RIDE_REQUEST.remove(SRC_LONG);
            }
            if (selectedEditText.getTag().equals("destination")) {
                Log.i("LocationPickActivity", "setLocationText: else set destination");
                RIDE_REQUEST.remove(DEST_ADD);
                RIDE_REQUEST.remove(DEST_LAT);
                RIDE_REQUEST.remove(DEST_LONG);
            }
        }

        if (isSettingLocationClick) {
            hideKeyboard();
            locationsRv.setVisibility(View.GONE);
        }
    }

    protected synchronized void buildGoogleApiClient() {
        Places.initialize(getApplicationContext(), getString(R.string.google_map_key));
        mGoogleApiClient = Places.createClient(this);
    }

    @OnClick({R.id.source, R.id.destination, R.id.reset_source, R.id.reset_destination, R.id.home_address_layout, R.id.work_address_layout, R.id.button2})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.source:
                break;
            case R.id.destination:
                break;
            case R.id.reset_source:
                Log.i("LocationPickActivity", "onViewClicked: from reset source");
                selectedEditText = source;
                source.requestFocus();
                setLocationText(null, null, isLocationRvClick, isSettingLocationClick);
                break;
            case R.id.reset_destination:
                Log.i("LocationPickActivity", "onViewClicked: from reset destination");
                destination.requestFocus();
                selectedEditText = destination;
                setLocationText(null, null, isLocationRvClick, isSettingLocationClick);
                break;
            case R.id.home_address_layout:
                if (home != null)
                    setLocationText(home.getAddress(),
                            new LatLng(home.getLatitude(), home.getLongitude()),
                            isLocationRvClick, isSettingLocationClick);
                break;
            case R.id.work_address_layout:
                if (work != null)
                    setLocationText(work.getAddress(),
                            new LatLng(work.getLatitude(), work.getLongitude()),
                            isLocationRvClick, isSettingLocationClick);
                break;
            case R.id.button2:

                Log.i("LocationPickActivity", "onViewClicked: SRC_ADD: " + s_address);
                Log.i("LocationPickActivity", "onViewClicked: SRC_LAT: " + s_latitude);
                Log.i("LocationPickActivity", "onViewClicked: SRC_LONG: " + s_longitude);


                if (!TextUtils.isEmpty(actionName) && actionName.equals(Constants.LocationActions.SELECT_HOME) || actionName.equals(Constants.LocationActions.SELECT_WORK)) {
                    Log.i("LocationPickActivity", "onViewClicked: if");
                    Intent intent = new Intent();
                    intent.putExtra(SRC_ADD, s_address);
                    intent.putExtra(SRC_LAT, s_latitude);
                    intent.putExtra(SRC_LONG, s_longitude);

                    setResult(AppCompatActivity.RESULT_OK, intent);
                    finish();

                } else {
                    Log.i("LocationPickActivity", "onViewClicked: else");

                    Log.i("LocationPickActivity", "RIDE_REQUEST: else SRC_ADD: " + RIDE_REQUEST.get(SRC_ADD));
                    Log.i("LocationPickActivity", "RIDE_REQUEST: else SRC_LAT: " + RIDE_REQUEST.get(SRC_LAT));
                    Log.i("LocationPickActivity", "RIDE_REQUEST: else SRC_LONG: " + RIDE_REQUEST.get(SRC_LONG));

                    Log.i("LocationPickActivity", "RIDE_REQUEST: else DEST_ADD: " + RIDE_REQUEST.get(DEST_ADD));
                    Log.i("LocationPickActivity", "RIDE_REQUEST: else DEST_LAT: " + RIDE_REQUEST.get(DEST_LAT));
                    Log.i("LocationPickActivity", "RIDE_REQUEST: else DEST_LONG: " + RIDE_REQUEST.get(DEST_LONG));

                    setResult(AppCompatActivity.RESULT_OK, new Intent());
                    finish();
                }

                break;
        }
    }

    @Override
    public void onCameraIdle() {
        Log.i("LocationPickActivity", "onCameraIdle: ");
        try {
            CameraPosition cameraPosition = mGoogleMap.getCameraPosition();
            if (isEnableIdle) {
                String address = getAddress(cameraPosition.target);
                System.out.println("onCameraIdle " + address);
                hideKeyboard();
                setLocationText(address, cameraPosition.target, isLocationRvClick, isSettingLocationClick);

//                setLocationTextAtSinglePlace(address,cameraPosition.target,isLocationRvClick,isSettingLocationClick);

            }
            isEnableIdle = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCameraMove() {
        System.out.println("LocationPickActivity.onCameraMove");
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        try {
            //      Google map custom style...
            googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.style_json));
        } catch (Resources.NotFoundException e) {
            Log.d("Map:Style", "Can't find style. Error: ");
        }
        this.mGoogleMap = googleMap;
        getLocationPermission();
        updateLocationUI();
        getDeviceLocation();
    }

    void getDeviceLocation() {
        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        mLastKnownLocation = task.getResult();
                        mGoogleMap.moveCamera(CameraUpdateFactory
                                .newLatLngZoom(new LatLng(
                                        mLastKnownLocation.getLatitude(),
                                        mLastKnownLocation.getLongitude()
                                ), DEFAULT_ZOOM));
                    } else {
                        Log.d("Map", "Current location is null. Using defaults.");
                        Log.e("Map", "Exception: %s", task.getException());
                        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    public void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            mLocationPermissionGranted = true;
        else{
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_ACCESS_FINE_LOCATION);

        }
    }

    private void updateLocationUI() {
        if (mGoogleMap == null) return;
        try {
            if (mLocationPermissionGranted) {
                mGoogleMap.setMyLocationEnabled(true);
                mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
                mGoogleMap.setOnCameraMoveListener(this);
                mGoogleMap.setOnCameraIdleListener(this);
            } else {
                mGoogleMap.setMyLocationEnabled(false);
                mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        if (requestCode == REQUEST_ACCESS_FINE_LOCATION)
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted = true;
                updateLocationUI();
                getDeviceLocation();
            }
    }


    @Override
    public void onBackPressed() {
        if (mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED)
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        else super.onBackPressed();
    }


   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.location_pick_menu, menu);
        return true;
    }*/

  /*  @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_done:
                if (!TextUtils.isEmpty(actionName) && actionName.equals(Constants.LocationActions.SELECT_HOME) || actionName.equals(Constants.LocationActions.SELECT_WORK)){
                    Intent intent = new Intent();
                    intent.putExtra(SRC_ADD, s_address);
                    intent.putExtra(SRC_LAT, s_latitude);
                    intent.putExtra(SRC_LONG, s_longitude);
                    setResult(AppCompatActivity.RESULT_OK, intent);
                    finish();
                } else {
                    setResult(AppCompatActivity.RESULT_OK, new Intent());
                    finish();
                }
                return true;
//            case android.R.id.home:
//                Toast.makeText(getApplicationContext(), "Back button clicked", Toast.LENGTH_SHORT).show();
//                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }*/


    @Override
    public void onSuccess(AddressResponse address) {
        if (address.getHome().isEmpty()) homeAddressLayout.setVisibility(View.GONE);
        else {
            home = address.getHome().get(address.getHome().size() - 1);
            homeAddress.setText(home.getAddress());
            homeAddressLayout.setVisibility(View.VISIBLE);
        }

        if (address.getWork().isEmpty()) workAddressLayout.setVisibility(View.GONE);
        else {
            work = address.getWork().get(address.getWork().size() - 1);
            workAddress.setText(work.getAddress());
            workAddressLayout.setVisibility(View.VISIBLE);
        }
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

}
