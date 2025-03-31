package com.example.solar;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class MainActivity extends AppCompatActivity {
    private static final String CHANNEL_ID = "solar_channel";
    private static final String SHARED_PREFS = "SolarPrefs";
    private static final String USERNAME_KEY = "username";
    private static final String IS_LOGGED_IN_KEY = "isLoggedIn";
    private ActivityResultLauncher<String> requestPermissionLauncher;
    private Button submit;
    private EditText user, pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean(IS_LOGGED_IN_KEY, false);

        if (isLoggedIn) {
            Intent intent = new Intent(MainActivity.this, Home.class);
            startActivity(intent);
            finish();
            return;
        }

        setContentView(R.layout.activity_main);

        submit = findViewById(R.id.login);
        user = findViewById(R.id.username);
        pass = findViewById(R.id.pass);
        createNotificationChannel();
        initializeSharedPreferences();
        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (!isGranted) {}

                }
        );
        submit.setEnabled(false);
        requestNotificationPermission();
        addTextWatchers();
        submit.setOnClickListener(v -> submit());
    }
    private void addTextWatchers() {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                validateFields();
            }
        };

        user.addTextChangedListener(textWatcher);
        pass.addTextChangedListener(textWatcher);
    }

    private void validateFields() {
        String username = user.getText().toString().trim();
        String password = pass.getText().toString().trim();
        submit.setEnabled(!username.isEmpty() && !password.isEmpty());
    }

    private void initializeSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        String savedUsername = sharedPreferences.getString(USERNAME_KEY, null);

        if (savedUsername != null) {
            Toast.makeText(this, "Welcome back, " + savedUsername + "!", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveUsername(String username) {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(IS_LOGGED_IN_KEY, true);
        editor.putString(USERNAME_KEY, username);
        editor.apply();
    }

    private void sendNotification() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("You are in!!😉")
                .setContentText("Login successful for Solar:Stable ⚡⚡⚡")
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(1, builder.build());
    }

    private void submit() {
        String username = user.getText().toString().trim();
        if (username.isEmpty()) {
            Toast.makeText(this, "Please enter a username!", Toast.LENGTH_SHORT).show();
            return;
        }
        saveUsername(username);

        sendNotification();
        Intent intent = new Intent(MainActivity.this, Home.class);
        startActivity(intent);
        finish();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Solar:Stable";
            String description = "Channel for Solar descriptions";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }
}
