package com.example.honeyimhome;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;


public class LocalSendSmsBroadcastReceiver extends BroadcastReceiver {

    private static final String INVALID_PHONE_CONTENT = "Phone or content invalid";
    private static final String SMS_PERMISSION_MSG = "No SMS permission granted";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (ActivityCompat
                .checkSelfPermission(context, Manifest.permission.SEND_SMS) ==
                PackageManager.PERMISSION_GRANTED) {
            String phone = intent.getStringExtra(MessageManager.PHONE_NUMBER_KEY);
            String content = intent.getStringExtra(MessageManager.SMS_CONTENT_KEY);
            if (phone == null || phone.isEmpty() || content == null || content.isEmpty()) {
                Log.e(MessageManager.ERROR_TAG, INVALID_PHONE_CONTENT);
            }
            MyApplication myApp = (MyApplication) context.getApplicationContext();
            myApp.getMessageManager().createNotificationChannel(context);
            Intent intentToOpen = new Intent(myApp, MainActivity.class);
            intentToOpen
                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(myApp, 0,
                    intentToOpen, 0);
            String ntfcBody = String.format("sending sms to %s: %s", phone, content);
            NotificationCompat.Builder builder = new NotificationCompat
                    .Builder(myApp, MessageManager.NTFC_CHANNEL_ID)
                    .setSmallIcon(R.drawable.notification_icon)
                    .setContentTitle(myApp.APP_NAME)
                    .setContentText(ntfcBody)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(ntfcBody))
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(myApp);
            notificationManager.notify(MessageManager.NTFC_ID, builder.build());
            myApp.getMessageManager().sendText(intent);
        } else {
            Log.e(MessageManager.ERROR_TAG, SMS_PERMISSION_MSG);
        }
    }
}

