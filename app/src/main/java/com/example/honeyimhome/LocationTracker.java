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
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import com.google.gson.Gson;
import com.google.android.gms.location.FusedLocationProviderClient;


public class LocationTracker implements LocationListener{

    public static final String LOCATION_CHANGED = "locationChanged";
    public static final String STOPPED_TRACKING = "stoppedTracking";
    public static final String SET_HOME = "setHome";
    public static final String CLEAR_HOME = "clearHome";
    private static final String SP_HOME_LOCATION = "homeLocation";
    private static final String SP_LOCATION_INFO = "locationInfo";
    private static final String SP_IS_TRACKING = "isOnTracking";

    private FusedLocationProviderClient fusedLocationClient;
    private Context appContext;
    private boolean isOnTracking;
    private LocationInfo locationInfo;
    private LocationInfo homeLocation;
    private LocationManager locationManager;
    private SharedPreferences sp;

    LocationTracker(Context context) {
        this.appContext = context;
        this.isOnTracking = false;
        this.locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        this.sp = PreferenceManager.getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        String json = sp.getString(SP_HOME_LOCATION, "");
        this.homeLocation = gson.fromJson(json, LocationInfo.class);
        isOnTracking = sp.getBoolean(SP_IS_TRACKING, false);
        json = sp.getString(SP_LOCATION_INFO, "");
        this.locationInfo = gson.fromJson(json, LocationInfo.class);
        if (this.locationInfo == null) {
            this.locationInfo = new LocationInfo();
        }
    }

    public void startTracking() {
        //add a basic check that assert you have the runtime location permission
        this.isOnTracking = true;
        sp.edit().putBoolean(SP_IS_TRACKING, isOnTracking).apply();
        boolean hasLocationPermissions = ActivityCompat.checkSelfPermission(appContext,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        if(hasLocationPermissions){
            if(locationManager!=null){
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        5000, 5, this);
                Gson gson = new Gson();
                sp.edit().putString(SP_LOCATION_INFO, gson.toJson(locationInfo)).apply();
            }
        }

//        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);
//        if (ActivityCompat.checkSelfPermission(appContext, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED) {
//            Log.e("Permission:", "Enable GPS amd Internet");
//            return;
//        }
//        fusedLocationClient.getLastLocation()
//                .addOnSuccessListener(activity, new OnSuccessListener<Location>() {
//                    @Override
//                    public void onSuccess(Location location) {
//                        // Got last known location. In some rare situations this can be null.
//                        if (location != null) {
//                            // Logic to handle location object
//                            locationInfo.setLangitude(location.getLongitude());
//                            locationInfo.setLatitude(location.getLatitude());
//                            locationInfo.setAccuracy(location.getAccuracy());
//                        }
//                    }
//                });
////        getLocation();
//        locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, LOCATION_RE);
//        broadcastStopTracking(appContext);
        }

        //which will start tracking the location and send a "started" boradcast intent
    //if yes continue to track location, if not just log some error to logcat and don't do anything

    public void stopTracking(){//which will stop tracking
        this.isOnTracking = false;
        sp.edit().putBoolean(SP_IS_TRACKING, isOnTracking).apply();
        locationManager.removeUpdates(this);
        Intent locationIntent = new Intent();
        locationIntent.setAction(STOPPED_TRACKING);
        appContext.sendBroadcast(locationIntent);
    }

    public boolean isOnTracking() {return this.isOnTracking; }

    public LocationInfo getLocationInfo(){return this.locationInfo;}

    public LocationInfo getHomeLocation(){return this.homeLocation;}

    public void setHomeLocation(){
//        this.homeLocation.setAccuracy(locationInfo.getAccuracy());
////        this.homeLocation.setLangitude(locationInfo.getLangitude());
////        this.homeLocation.setLatitude(locationInfo.getLatitude());
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
        Intent locationIntent = new Intent();
        locationIntent.setAction(LOCATION_CHANGED);
        appContext.sendBroadcast(locationIntent);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(appContext, "Please Enable GPS amd Internet", Toast.LENGTH_SHORT).show();
    }

    public LocationInfo updateData(){
        Gson gson = new Gson();
        String json = sp.getString(SP_HOME_LOCATION, "");
        homeLocation = gson.fromJson(json, LocationInfo.class);
        json = sp.getString(SP_LOCATION_INFO, "");
        locationInfo = gson.fromJson(json, LocationInfo.class);
        if (this.locationInfo == null) {
            this.locationInfo = new LocationInfo();
        }
        isOnTracking = sp.getBoolean(SP_IS_TRACKING, false);
        return homeLocation;

    }

//    private void startLocationUpdates() {
//        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
//    }


    // Listen to Android system event - permission change- while tracking, whenever a new
    // location has been received, send a "new_location" broadcast Intent
    // Broadcast the event to all the activities

//    private void broadcastTracking(Context context) {
//        Intent intent = new Intent();
//        intent.putExtra("locationInfoLatitude", locationInfo.getLatitude());
//        intent.putExtra("locationInfoLangitude", locationInfo.getLangitude());
//        intent.putExtra("locationInfoAccuracy()", locationInfo.getAccuracy());
//        intent.setAction(NEW_LOCATION);
//        context.sendBroadcast(intent);
//    }

//    void getLocation(){
//        try{
//            locationManager = (LocationManager) appContext.getSystemService(appContext.LOCATION_SERVICE);
//            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 5, this);
//        }catch(SecurityException e){
//            e.printStackTrace();
//        }
//    }

}
