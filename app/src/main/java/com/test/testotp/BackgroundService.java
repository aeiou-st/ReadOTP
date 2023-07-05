package com.test.testotp;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

public class BackgroundService extends Service {
    private SMSReceiver smsReceiver;
    private MainActivity mainActivity;

    @Override
    public void onCreate() {
        super.onCreate();
        smsReceiver = new SMSReceiver();
        registerReceiver(smsReceiver, new IntentFilter("android.provider.Telephony.SMS_RECEIVED"));
        Log.d("BackgroundService", "SMSReceiver registered");

    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(smsReceiver);
        Log.d("BackgroundService", "SMSReceiver unregistered");

    }
}