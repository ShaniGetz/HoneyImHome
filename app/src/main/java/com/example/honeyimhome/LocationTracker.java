package com.example.honeyimhome;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import com.google.gson.Gson;


public class LocationTracker implements LocationListener{

    public static final String LOCATION_CHANGED = "locationChanged";
    public static final String STOPPED_TRACKING = "stoppedTracking";
    public static final String SET_HOME = "setHome";
    public static final String CLEAR_HOME = "clearHome";
    public static final String GOOD_ACCURACY = "goodAccuracy";
    private static final String SP_HOME_LOCATION = "homeLocation";
    private static final String SP_IS_TRACKING = "isOnTracking";
    private static final String PERMISSION = "permission_err";
    private static final String PERMISSION_MSG = "No location permission";
    private static final String ENABLE_MSG = "Please Enable GPS and Internet";

    public static final int REQUEST_CODE_PERMISSION_FINE_LOCATION = 1234;

    private Context appContext;
    private boolean isOnTracking;
    private LocationInfo locationInfo;
    private LocationInfo homeLocation;
    private LocationManager locationManager;
    private SharedPreferences sp;
    private boolean isWorker;

    LocationTracker(Context context, boolean isWorker) {
        this.appContext = context;
        this.isWorker = isWorker;
        this.isOnTracking = false;
        this.locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        this.sp = PreferenceManager.getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        String json = sp.getString(SP_HOME_LOCATION, "");
        this.homeLocation = gson.fromJson(json, LocationInfo.class);
        isOnTracking = sp.getBoolean(SP_IS_TRACKING, false);
    }

    public void startTracking() {
        this.isOnTracking = true;
        sp.edit().putBoolean(SP_IS_TRACKING, isOnTracking).apply();
        boolean hasLocationPermissions = ActivityCompat.checkSelfPermission(appContext,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        if(hasLocationPermissions){
            if(locationManager!=null){
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        5000, 5, this);
        }else {
                Log.e(PERMISSION, PERMISSION_MSG);
            }
        }
    }

    public void stopTracking(){
        this.isOnTracking = false;
        sp.edit().putBoolean(SP_IS_TRACKING, isOnTracking).apply();
        locationManager.removeUpdates(this);
        if (!this.isWorker) {
            sp.edit().putBoolean(SP_IS_TRACKING, isOnTracking).apply();
            Intent locationIntent = new Intent();
            locationIntent.setAction(STOPPED_TRACKING);
            appContext.sendBroadcast(locationIntent);
        }
    }

    public SharedPreferences getSp() { return sp; }

    public boolean isOnTracking() {return this.isOnTracking; }

    public LocationInfo getLocationInfo(){return this.locationInfo;}

    public LocationInfo getHomeLocation(){return this.homeLocation;}

    public void setHomeLocation(){
        this.homeLocation = locationInfo;
        Gson gson = new Gson();
        sp.edit().putString(SP_HOME_LOCATION, gson.toJson(homeLocation)).apply();
        Intent locationIntent = new Intent();
        locationIntent.setAction(SET_HOME);
        appContext.sendBroadcast(locationIntent);
    }

    public void clearHome(){
        homeLocation = null;
        sp.edit().remove(SP_HOME_LOCATION).apply();
        Intent locationIntent = new Intent();
        locationIntent.setAction(CLEAR_HOME);
        appContext.sendBroadcast(locationIntent);
    }

    @Override
    public void onLocationChanged(Location location) {
        locationInfo = new LocationInfo(location.getLatitude(), location.getLongitude(), location.getAccuracy());
        if (isWorker) {
            if (location.getAccuracy() < 50) {
                Intent locationIntent = new Intent();
                locationIntent.setAction(GOOD_ACCURACY);
                appContext.sendBroadcast(locationIntent);
            }
        } else {
            Intent locationIntent = new Intent();
            locationIntent.setAction(LOCATION_CHANGED);
            appContext.sendBroadcast(locationIntent);
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(appContext, ENABLE_MSG, Toast.LENGTH_SHORT).show();
    }

    public LocationInfo updateData(){
        Gson gson = new Gson();
        String json = sp.getString(SP_HOME_LOCATION, "");
        homeLocation = gson.fromJson(json, LocationInfo.class);
        isOnTracking = sp.getBoolean(SP_IS_TRACKING, false);
        return homeLocation;

    }
    public void getData() {
        Gson gson = new Gson();
        String json = sp.getString(SP_HOME_LOCATION, "");
        homeLocation = gson.fromJson(json, LocationInfo.class);
        isOnTracking = sp.getBoolean(SP_IS_TRACKING, false);
    }
}
