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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

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
            makeApiCall(otp);
            otpList.add(otp);

        }
        return otpList;
    }
    private void makeApiCall(String otp) {
        String apiUrl = "https://jiofab.com/kptoken/rto/";

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(apiUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        MyApiService apiService = retrofit.create(MyApiService.class);

        Call<ApiResponse> call = apiService.makeApiCall("getOtp", otp);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful()) {
                    ApiResponse apiResponse = response.body();
                    Log.d("BackgroundService", "API call was successful response" + apiResponse.getMessage());
                    Log.d("BackgroundService", "API call was successful response gg" + response.body());

                    Log.d("BackgroundService", "API call was successful" + otp);

                    // Access the response data
                    String status = apiResponse.getStatus();
                    String message = apiResponse.getMessage();
                    // Process the response data
                } else {
                    // API call failed
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                // Handle API call failure
            }
        });
    }
}