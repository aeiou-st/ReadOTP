package com.test.testotp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SMSReceiver extends BroadcastReceiver {
    private MainActivity mainActivity;

    public void setMainActivity(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("BackgroundService", "SMSReceiver recevied");

        if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                Object[] pdus = (Object[]) bundle.get("pdus");
                if (pdus != null) {
                    for (Object pdu : pdus) {
                        SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdu);
                        Log.d("BackgroundService", "SMSReceiver recevied SMS message" + smsMessage);

                        String message = smsMessage.getMessageBody();
                        Log.d("BackgroundService", "SMSReceiver recevied SMS" + message);

                        // Process the message and extract the OTP
                        String otp = extractOTP(message);
                        Log.d("BackgroundService", "SMSReceiver recevied AFTER EXTRACT" + otp);


                        if (otp != null && !otp.isEmpty() && mainActivity != null) {
                            mainActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Log.d("BackgroundService", "SMSReceiver recevied" + otp);
                                    mainActivity.displayOTP(otp);
                                    Log.d("BackgroundService", "SMSReceiver recevied" + otp);

                                }
                            });
                        }
                    }
                }
            }
        }
    }

    private void displayOTP(String otp) {
        if (mainActivity != null) {
            mainActivity.displayOTP(otp);
        }
    }

    private String extractOTP(String message) {
        // Implement your logic to extract the OTP from the message
        // This can be done using regular expressions or other techniques
        // Return the extracted OTP as a string
        // In this example, we assume the OTP is a 6-digit number
        Log.d("BackgroundService", "SMSReceiver recevied Extract OTP " + message);

        String otp = null;
        Pattern pattern = Pattern.compile("\\b\\d{6}\\b");
        Matcher matcher = pattern.matcher(message);
        if (matcher.find()) {
            Log.d("BackgroundService", "SMSReceiver recevied Extract matcher " + matcher);

            otp = matcher.group();
            Log.d("BackgroundService", "SMSReceiver recevied Extract OTP " + otp);
        }else {
            Log.d("BackgroundService", "SMSReceiver recevied Extract ELSE " + otp);

        }
        Log.d("BackgroundService", "SMSReceiver recevied Extract OTP RETURN " + otp);

        return otp;
    }
}