package com.example.solar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class Home extends AppCompatActivity {

    private static final String SHARED_PREFS = "SolarPrefs";
    private static final String USERNAME_KEY = "username";
    public static final String PROVIDER_KEY = "provider";
    public static final String EFFICIENCY_KEY = "efficiency";

    private ImageView profileIcon, logoutIcon;
    private Button tataButton, adaniButton, vikramButton,data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        profileIcon = findViewById(R.id.profileIcon);
        logoutIcon = findViewById(R.id.logoutIcon);
        tataButton = findViewById(R.id.tataButton);
        adaniButton = findViewById(R.id.adaniButton);
        vikramButton = findViewById(R.id.vikramButton);
        data = findViewById(R.id.data);

        profileIcon.setOnClickListener(v -> showUsername());
        logoutIcon.setOnClickListener(v -> logoutUser());

        tataButton.setOnClickListener(v -> openCalculationPage("Tata Power", 18.0));
        adaniButton.setOnClickListener(v -> openCalculationPage("Adani Solar", 22.4));
        vikramButton.setOnClickListener(v -> openCalculationPage("Vikram Solar", 23.02));
        data.setOnClickListener(v -> {
            Intent intent = new Intent(Home.this,  viewData.class);
            startActivity(intent);
            finish();
        });
    }
    private void showUsername() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        String username = sharedPreferences.getString(USERNAME_KEY, null);

        if (username != null) {
            new AlertDialog.Builder(this)
                    .setTitle("Your Profile")
                    .setMessage("Username: " + username)
                    .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                    .create()
                    .show();
        } else {
            Toast.makeText(this, "No username found!", Toast.LENGTH_SHORT).show();
        }
    }
    private void logoutUser() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        Toast.makeText(this, "Logged out successfully!", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(Home.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
    private void openCalculationPage(String provider, double efficiency) {
        Intent intent = new Intent(Home.this, calc.class);
        intent.putExtra(PROVIDER_KEY, provider);      // Pass the provider's name
        intent.putExtra(EFFICIENCY_KEY, efficiency);  // Pass the provider's efficiency
        startActivity(intent);
    }
}



