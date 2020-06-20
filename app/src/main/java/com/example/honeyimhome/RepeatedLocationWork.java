package com.example.honeyimhome;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.concurrent.futures.CallbackToFutureAdapter;
import androidx.core.app.ActivityCompat;
import androidx.work.ListenableWorker;
import androidx.work.WorkerParameters;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.Gson;

public class RepeatedLocationWork extends ListenableWorker {

    private static final String SP_PREV_LOCATION = "prevLocation";

    private CallbackToFutureAdapter.Completer<Result> callback = null;
    private BroadcastReceiver broadcastReceiver;
    private Context context;
    private MyApplication myApp;
    private LocationTracker tracker;
    private LocationInfo prevLocation;

    public RepeatedLocationWork(Context context, WorkerParameters params) {
        super(context, params);
        this.myApp = (MyApplication) context.getApplicationContext();
        this.context = context;
        tracker = new LocationTracker(context, true);
    }

    @Override
    public ListenableFuture<Result> startWork() {
        ListenableFuture<Result> future = CallbackToFutureAdapter
                .getFuture(new CallbackToFutureAdapter.Resolver<Result>() {
                    @Nullable
                    @Override
                    public Object attachCompleter(@NonNull CallbackToFutureAdapter
                            .Completer<Result> completer) throws Exception {
                        callback = completer;
                        return null;
                    }
                });
        MessageManager messageManager = myApp.getMessageManager();
        LocationTracker locationTracker = myApp.getLocationTracker();
        if (!messageManager.hasSmsPermission() || !(ActivityCompat
                .checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED)) {
            callback.set(Result.success());
            return future;
        }
        messageManager.getPhoneData();
        locationTracker.getData();
        if (messageManager.getPhoneNumber() == null || locationTracker.getHomeLocation() == null) {
            callback.set(Result.success());
            return future;
        }
        placeReceiver();
        tracker.startTracking();
        return future;
    }

    private void placeReceiver() {
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                onReceiveActions();
            }
        };
        IntentFilter filter = new IntentFilter(LocationTracker.GOOD_ACCURACY);
        context.registerReceiver(broadcastReceiver, filter);
    }

    private void onReceiveActions() {
        tracker.stopTracking();
        LocationInfo curLocation = tracker.getLocationInfo();
        SharedPreferences prefs = tracker.getSp();
        Gson gson = new Gson();
        String json = prefs.getString(SP_PREV_LOCATION, "");
        prevLocation = gson.fromJson(json, LocationInfo.class);
        if (prevLocation == null || distance(prevLocation, curLocation)[0] < 50) {
            finishWork(prefs, curLocation);
            return;
        }
        if (distance(curLocation, myApp.getLocationTracker().getHomeLocation())[0] < 50) {
            Intent smsIntent = new Intent();
            smsIntent.setAction(MessageManager.SEND_SMS);
            smsIntent.putExtra(MessageManager.PHONE_NUMBER_KEY,
                    myApp.getMessageManager().getPhoneNumber());
            smsIntent.putExtra(MessageManager.SMS_CONTENT_KEY, "Honey I'm Home!");
            myApp.getLocalBroadcastManager().sendBroadcast(smsIntent);
        }
        finishWork(prefs, curLocation);
    }

    private void finishWork(SharedPreferences prefs, LocationInfo curLocation) {
        Gson gson = new Gson();
        prefs.edit().putString(SP_PREV_LOCATION, gson.toJson(curLocation)).apply();
        context.unregisterReceiver(broadcastReceiver);
        callback.set(Result.success());
    }

//    private void updatePrevLocation(SharedPreferences prefs, LocationInfo curLocation) {
//        Gson gson = new Gson();
//        prefs.edit().putString(SP_PREV_LOCATION, gson.toJson(curLocation)).apply();
//    }

    private float[] distance(LocationInfo first, LocationInfo second) {
        double fLat = first.getLatitude();
        double fLong = first.getLangitude();
        double sLat = second.getLatitude();
        double sLong = second.getLangitude();
        float[] result = new float[1];
        Location.distanceBetween(fLat, fLong, sLat, sLong, result);
        return result;
    }

}
