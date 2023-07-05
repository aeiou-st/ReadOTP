package com.test.testotp;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

public class BackgroundService extends Service {
    private SMSReceiver smsReceiver;
    private MainActivity mainActivity;
    private final IBinder binder = new LocalBinder();

    @Override
    public void onCreate() {
        super.onCreate();
        smsReceiver = new SMSReceiver();
        smsReceiver.setMainActivity(mainActivity);
        registerReceiver(smsReceiver, new IntentFilter("android.provider.Telephony.SMS_RECEIVED"));
        Log.d("BackgroundService", "SMSReceiver registered");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(smsReceiver);
        Log.d("BackgroundService", "SMSReceiver unregistered");
    }

    public class LocalBinder extends Binder {
        BackgroundService getService() {
            return BackgroundService.this;
        }
    }

    public void setMainActivity(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        if (smsReceiver != null) {
            smsReceiver.setMainActivity(mainActivity);
        }
    }

    public void removeMainActivity() {
        this.mainActivity = null;
        if (smsReceiver != null) {
            smsReceiver.removeMainActivity();
        }
    }
}
