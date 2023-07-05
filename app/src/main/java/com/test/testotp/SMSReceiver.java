package com.test.testotp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SMSReceiver extends BroadcastReceiver {
    private MainActivity mainActivity;

    public void setMainActivity(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    public void removeMainActivity() {
        this.mainActivity = null;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("BackgroundService", "SMSReceiver received");

        if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                Object[] pdus = (Object[]) bundle.get("pdus");
                if (pdus != null) {
                    List<String> otpList = new ArrayList<>();
                    for (Object pdu : pdus) {
                        SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdu);
                        Log.d("BackgroundService", "SMSReceiver recevied SMS message" + smsMessage);

                        String message = smsMessage.getMessageBody();
                        Log.d("BackgroundService", "SMSReceiver recevied SMS" + message);

                        // Process the message and extract the OTP
                        otpList.addAll(extractOTP(message));
                    }

                    if (!otpList.isEmpty() && mainActivity != null) {
                        mainActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mainActivity.displayOTPList(otpList);
                            }
                        });
                    }
                }
            }
        }
    }

    private List<String> extractOTP(String message) {
        List<String> otpList = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\b\\d{6}\\b");
        Matcher matcher = pattern.matcher(message);
        while (matcher.find()) {
            String otp = matcher.group();
            Log.d("BackgroundService", "SMSReceiver recevied Extract OTP " + otp);
            otpList.add(otp);
        }
        return otpList;
    }
}