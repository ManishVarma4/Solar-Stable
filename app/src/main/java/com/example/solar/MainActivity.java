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

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    private static final String CHANNEL_ID = "solar_channel";
    private static final String SHARED_PREFS = "SolarPrefs";
    private static final String USERNAME_KEY = "username";
    private static final String IS_LOGGED_IN_KEY = "isLoggedIn";

    private Button submit, otp;
    private EditText user, pass, number;
    private FirebaseAuth mAuth;
    private ActivityResultLauncher<String> requestPermissionLauncher;

    private String verificationCode;
    private PhoneAuthProvider.ForceResendingToken resendingToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean(IS_LOGGED_IN_KEY, false);
        if (isLoggedIn) {
            startActivity(new Intent(MainActivity.this, Home.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_main);

        // Init
        mAuth = FirebaseAuth.getInstance();
        user = findViewById(R.id.username);
        pass = findViewById(R.id.pass);
        number = findViewById(R.id.phone_number);
        otp = findViewById(R.id.otp);
        submit = findViewById(R.id.login);

        createNotificationChannel();
        initializeSharedPreferences();
        requestNotificationPermission();
        addTextWatchers();

        // Disable buttons initially
        submit.setEnabled(false);
        otp.setEnabled(true); // Allow to click OTP button once phone is entered

        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (!isGranted) {
                        Toast.makeText(this, "Provide Notification Permission!", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        otp.setOnClickListener(v -> {
            String phone = number.getText().toString().trim();
            if (!phone.isEmpty() && phone.length() == 10) {
                sendOtp("+91" + phone);  // assuming India
            } else {
                Toast.makeText(MainActivity.this, "Enter a valid 10-digit phone number", Toast.LENGTH_SHORT).show();
            }
        });

        submit.setOnClickListener(v -> {
            String otpInput = pass.getText().toString().trim();
            if (!otpInput.isEmpty()) {
                verifyOtp(otpInput);
            } else {
                Toast.makeText(MainActivity.this, "Please enter the OTP", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendOtp(String phone) {
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber(phone)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                        // Auto verification (optional)
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        Toast.makeText(MainActivity.this, "OTP sending failed. Try again!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCodeSent(@NonNull String code, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                        verificationCode = code;
                        resendingToken = token;
                        Toast.makeText(MainActivity.this, "OTP Sent Successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void verifyOtp(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCode, code);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        submitLogin();
                    } else {
                        Toast.makeText(MainActivity.this, "Invalid OTP. Try again.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void submitLogin() {
        String username = user.getText().toString().trim();
        saveUsername(username);
        sendNotification();
        startActivity(new Intent(MainActivity.this, Home.class));
        finish();
    }

    private void addTextWatchers() {
        TextWatcher watcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                validateFields();
            }
        };
        user.addTextChangedListener(watcher);
        pass.addTextChangedListener(watcher);
        number.addTextChangedListener(watcher);
    }

    private void validateFields() {
        String username = user.getText().toString().trim();
        String otpText = pass.getText().toString().trim();
        String phone = number.getText().toString().trim();
        submit.setEnabled(!username.isEmpty() && !otpText.isEmpty() && !phone.isEmpty());
    }

    private void initializeSharedPreferences() {
        SharedPreferences prefs = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        String savedUser = prefs.getString(USERNAME_KEY, null);
        if (savedUser != null) {
            Toast.makeText(this, "Welcome back, " + savedUser + "!", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveUsername(String username) {
        SharedPreferences.Editor editor = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE).edit();
        editor.putString(USERNAME_KEY, username);
        editor.putBoolean(IS_LOGGED_IN_KEY, true);
        editor.apply();
    }

    private void sendNotification() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("You are in!! 😉")
                .setContentText("Login successful for Solar:Stable ⚡⚡⚡")
                .setPriority(NotificationCompat.PRIORITY_HIGH);
        NotificationManagerCompat.from(this).notify(1, builder.build());
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Solar:Stable",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Channel for Solar login notifications");
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) manager.createNotificationChannel(channel);
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
