package com.test.testotp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int SMS_PERMISSION_REQUEST_CODE = 1;
    private Button startButton;
    private TextView otpTextView;
    private BackgroundService backgroundService;
    private boolean isServiceBound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startButton = findViewById(R.id.startButton);
        otpTextView = findViewById(R.id.otpTextView);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkSmsPermissions()) {
                    startBackgroundService();
                } else {
                    requestSmsPermissions();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        bindToBackgroundService();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindFromBackgroundService();
    }

    private boolean checkSmsPermissions() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED;
    }

    public void displayOTPList(List<String> otpList) {
        Log.d("BackgroundService", "MainActivity Display OTPLIST  " + otpList.toString());
        StringBuilder otps = new StringBuilder();
        for (String otp : otpList) {
            Log.d("BackgroundService", "MainActivity Display OTPLIST  " + otp.toString());
            otps.append(otp).append("\n");
        }
        otpTextView.setText("OTP List:\n" + otps.toString());
    }

    private void requestSmsPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.RECEIVE_SMS,
                Manifest.permission.READ_SMS
        }, SMS_PERMISSION_REQUEST_CODE);
    }

    private void startBackgroundService() {
        Intent serviceIntent = new Intent(this, BackgroundService.class);
        startService(serviceIntent);
    }

    private void bindToBackgroundService() {
        Intent serviceIntent = new Intent(this, BackgroundService.class);
        bindService(serviceIntent, serviceConnection, BIND_AUTO_CREATE);
    }

    private void unbindFromBackgroundService() {
        if (isServiceBound) {
            unbindService(serviceConnection);
            isServiceBound = false;
        }
    }

    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            BackgroundService.LocalBinder binder = (BackgroundService.LocalBinder) service;
            backgroundService = binder.getService();
            backgroundService.setMainActivity(MainActivity.this);
            isServiceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            backgroundService.removeMainActivity();
            backgroundService = null;
            isServiceBound = false;
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == SMS_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startBackgroundService();
            } else {
                Toast.makeText(this, "SMS permissions denied. The app cannot function properly.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
