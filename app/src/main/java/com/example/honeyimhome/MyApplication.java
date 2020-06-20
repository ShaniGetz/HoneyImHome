package com.example.honeyimhome;

import android.app.Application;
import android.content.IntentFilter;

import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Operation;

import java.util.concurrent.TimeUnit;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class MyApplication extends Application {
    public final String APP_NAME = "Honey I'm Home!";

    private LocationTracker locationTracker;
    private MessageManager messageManager;
    private LocalBroadcastManager localBroadcastManager;

    @Override
    public void onCreate() {
        super.onCreate();
        locationTracker = new LocationTracker(this, false);
        messageManager = new MessageManager(this);
        setSmsBroadcastReceiver();
        setRepeatedWork();
    }

    public LocationTracker getLocationTracker() {
        return this.locationTracker;
    }

    public MessageManager getMessageManager() {
        return this.messageManager;
    }

    public LocalBroadcastManager getLocalBroadcastManager(){
        return localBroadcastManager;
    }

    private void setSmsBroadcastReceiver() {
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        IntentFilter filter = new IntentFilter(MessageManager.SEND_SMS);
        localBroadcastManager.registerReceiver(new LocalSendSmsBroadcastReceiver(), filter);
    }

    private void setRepeatedWork() {
        PeriodicWorkRequest locationWorkRequest = new PeriodicWorkRequest
                        .Builder(RepeatedLocationWork.class, 15, TimeUnit.MINUTES).build();
        final Operation locationWork = WorkManager.getInstance(this).enqueueUniquePeriodicWork("locationWork",
                        ExistingPeriodicWorkPolicy.REPLACE, locationWorkRequest);
    }
}
