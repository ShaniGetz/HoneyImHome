package com.example.honeyimhome;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;

public class MessageManager {

    public static final int REQUEST_CODE_PERMISSION_SEND_TEXT = 1122;

    public static final String PHONE_NUMBER_KEY = "phoneNumber";
    public static final String SMS_CONTENT_KEY = "smsContent";
    public static final String ERROR_TAG = "SmsBroadcastReceiver";
    private static final String SP_PHONE = "phoneNumber";
    public static final String SEND_SMS = "sendSms";

    public static final String NTFC_CHANNEL_NAME = "HoneyImHome!";
    public static final String NTFC_CHANNEL_ID = "smsNtfc";
    public static final String NTFC_CHANNEL_DESC = "Notification Channel for SMS";
    public static final int NTFC_ID = 5;
    public static final String CHANNEL_ERR_MSG = "NTFC Channel Creation Error";

    private Context context;
    private SmsManager smsManager;
    private String phoneNumber;
    private static SharedPreferences sp;

    public MessageManager(Context context) {
        this.context = context;
        smsManager = SmsManager.getDefault();
        sp = PreferenceManager.getDefaultSharedPreferences(context);
        this.phoneNumber = sp.getString(SP_PHONE, "");
    }
    
    public boolean hasSmsPermission() {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS) ==
                PackageManager.PERMISSION_GRANTED;
    }

    public String getPhoneNumber() { return phoneNumber; }

    public void setPhoneNumber(String number) {
        this.phoneNumber = number;
        sp.edit().putString(SP_PHONE, phoneNumber).apply();
    }

    public void getPhoneData() {
        this.phoneNumber = sp.getString(SP_PHONE, "");
    }

    public void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String description = NTFC_CHANNEL_DESC;
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(NTFC_CHANNEL_ID, NTFC_CHANNEL_NAME, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            } else {
                Log.e(ERROR_TAG, CHANNEL_ERR_MSG);
            }
        }
    }

    public void sendText(Intent intent) {
        String content = intent.getStringExtra(SMS_CONTENT_KEY);
        PendingIntent sentIntent = PendingIntent
                .getActivity(context, REQUEST_CODE_PERMISSION_SEND_TEXT, intent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
        smsManager.sendTextMessage(phoneNumber, null, content, sentIntent, null);
    }


}
