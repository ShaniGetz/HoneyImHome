package com.example.honeyimhome;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private BroadcastReceiver broadcastReceiver;
    private LocationTracker locationTracker;
    private Activity activity = this;
    private Button trackLocationButton;
    private Button setHomeButton;
    private Button clearHomeButton;
    private TextView latitude;
    private TextView longitude;
    private TextView accuracy;
    private TextView homeLocationLatitude;
    private TextView homeLocationLongitude;


    private static final String PERMISSION_MSG = "Application will not run without location permission";
    private static final String REQUIRED_MSG = "Application required to access location";

    public static final int REQUEST_CODE_PERMISSION_FINE_LOCATION = 1234;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        locationTracker = new LocationTracker(getApplicationContext());
        setAllViews();
        setBroadcastReceiver();
    }

    private void setAllViews() {
        trackLocationButton = findViewById(R.id.trackingButton);
        setHomeButton = findViewById(R.id.setHomeButton);
        clearHomeButton = findViewById(R.id.clearHomeButton);
        latitude = findViewById(R.id.latitude);
        longitude = findViewById(R.id.longitude);
        accuracy = findViewById(R.id.accuracy);
        homeLocationLatitude = findViewById(R.id.homeLocationLatitude);
        homeLocationLongitude = findViewById(R.id.homeLocationLongitude);
    }

    private void setBroadcastReceiver() {
        broadcastReceiver = new locationBroadcastReceiver();
        IntentFilter filter = new IntentFilter(LocationTracker.LOCATION_CHANGED);
        filter.addAction(LocationTracker.STOPPED_TRACKING);
        filter.addAction(LocationTracker.SET_HOME);
        filter.addAction(LocationTracker.CLEAR_HOME);
        this.registerReceiver(broadcastReceiver, filter);
    }

    private class locationBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action == null) {
                return;
            }
            switch (action) {
                case LocationTracker.LOCATION_CHANGED:
                    trackingButton(true);
                    updateLocationInfo();
                    if (locationTracker.getLocationInfo().getAccuracy() < 50) {
                        setHomeButton(true);
                    }
                    showLocationInfoView(true);
                    break;
                case LocationTracker.STOPPED_TRACKING:
                    trackingButton(false);
                    setHomeButton(false);
                    showLocationInfoView(false);
                    break;
                case LocationTracker.SET_HOME:
                    clearHomeButton(true);
                    updateHomeLocation(false);
                    break;
                case LocationTracker.CLEAR_HOME:
                    clearHomeButton(false);
                    updateHomeLocation(true);
                    break;
            }
        }
    }

        private void trackingButton(boolean startTracking) {
            if (startTracking) {
                trackLocationButton.setText(getResources().getString(R.string.trackingButtonStop));
            } else {
                trackLocationButton.setText(getResources().getString(R.string.trackingButtonStart));
            }
        }

        private void setHomeButton(boolean setHome) {
            if (setHome) {
                setHomeButton.setClickable(true);
                setHomeButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            } else {
                setHomeButton.setClickable(false);
                setHomeButton.setBackgroundColor(getResources().getColor(R.color.grey));
            }
        }

    private void clearHomeButton(boolean clearHome) {
        if (clearHome) {
            clearHomeButton.setClickable(true);
            clearHomeButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        } else {
            clearHomeButton.setClickable(false);
            clearHomeButton.setBackgroundColor(getResources().getColor(R.color.grey));
        }
    }

        private void updateLocationInfo() {
            LocationInfo locationInfo = locationTracker.getLocationInfo();
            latitude.setText(String.valueOf(locationInfo.getLatitude()));
            longitude.setText(String.valueOf(locationInfo.getLangitude()));
            accuracy.setText(String.valueOf(locationInfo.getAccuracy()));
        }

    private void updateHomeLocation(boolean clean) {
        TextView homeLocationTitle = findViewById(R.id.homeLocationTitle);
        if (clean) {
            homeLocationLatitude.setText(getResources().getString(R.string.defaultInfo));
            homeLocationLongitude.setText(getResources().getString(R.string.defaultInfo));
            homeLocationTitle.setVisibility(View.INVISIBLE);
        } else {
            LocationInfo homeLocation = locationTracker.getHomeLocation();
            homeLocationLatitude.setText(String.valueOf(homeLocation.getLatitude()));
            homeLocationLongitude.setText(String.valueOf(homeLocation.getLangitude()));
            homeLocationTitle.setVisibility(View.VISIBLE);
        }
    }

        private void showLocationInfoView(boolean ifShow) {
            TextView yourLocation = findViewById(R.id.yourLocation);
            TextView latitudeTitle = findViewById(R.id.latitudeTitle);
            TextView longitudeTitle = findViewById(R.id.longitudeTitle);
            TextView accuracyTitle = findViewById(R.id.accuracyTitle);
            TextView homeLocationTitle = findViewById(R.id.homeLocationTitle);
            if (ifShow) {
                setHomeButton.setVisibility(View.VISIBLE);
                clearHomeButton.setVisibility(View.VISIBLE);
                yourLocation.setVisibility(View.VISIBLE);
                latitudeTitle.setVisibility(View.VISIBLE);
                accuracyTitle.setVisibility(View.VISIBLE);
                longitudeTitle.setVisibility(View.VISIBLE);
                accuracyTitle.setVisibility(View.VISIBLE);
                latitude.setVisibility(View.VISIBLE);
                longitude.setVisibility(View.VISIBLE);
                accuracy.setVisibility(View.VISIBLE);
                homeLocationTitle.setVisibility(View.VISIBLE);
                homeLocationLatitude.setVisibility(View.VISIBLE);
                homeLocationLongitude.setVisibility(View.VISIBLE);

            } else {
                setHomeButton.setVisibility(View.INVISIBLE);
                clearHomeButton.setVisibility(View.INVISIBLE);
                yourLocation.setVisibility(View.INVISIBLE);
                latitudeTitle.setVisibility(View.INVISIBLE);
                accuracyTitle.setVisibility(View.INVISIBLE);
                longitudeTitle.setVisibility(View.INVISIBLE);
                accuracyTitle.setVisibility(View.INVISIBLE);
                latitude.setVisibility(View.INVISIBLE);
                longitude.setVisibility(View.INVISIBLE);
                accuracy.setVisibility(View.INVISIBLE);
                homeLocationTitle.setVisibility(View.INVISIBLE);
                homeLocationLongitude.setVisibility(View.INVISIBLE);
                homeLocationLatitude.setVisibility(View.INVISIBLE);
            }
        }

        public void toastMessage(String message) {
            Toast newToast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
            newToast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
            ViewGroup group = (ViewGroup) newToast.getView();
            TextView messageTextView = (TextView) group.getChildAt(0);
            messageTextView.setTextSize(16);
            newToast.show();
        }

        public void OnTrackLocationButtonClick(View view) {
            if (locationTracker.isOnTracking()) {
                locationTracker.stopTracking();
            } else {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {
                    locationTracker.startTracking();
                } else {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION)) {
                        toastMessage(REQUIRED_MSG);
                    }
                    ActivityCompat.requestPermissions(activity,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_PERMISSION_FINE_LOCATION);
                }
            }
        }

        public void OnSetHomeButtonClick(View view) {
            locationTracker.setHomeLocation();
        }

        public void OnClearHomeButtonClick(View view){
            locationTracker.clearHome();
        }


    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            locationTracker.startTracking();
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                toastMessage(PERMISSION_MSG);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (locationTracker.isOnTracking()) {
            locationTracker.stopTracking();
        }
        unregisterReceiver(broadcastReceiver);//remove the registration in onDestroy()
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (locationTracker.isOnTracking()) {
            locationTracker.startTracking();
        } else {
            showLocationInfoView(false);
        }
        updateLocationInfo();
        LocationInfo homeLocation = locationTracker.updateData();
        if(homeLocation == null){
            clearHomeButton(false);
        }else{
            clearHomeButton(true);
            updateHomeLocation(false);
        }
        if(locationTracker.getLocationInfo().getAccuracy()<50){
            setHomeButton(true);
        }else{
            setHomeButton(false);
        }
    }
}
