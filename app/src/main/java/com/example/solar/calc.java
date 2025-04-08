package com.example.solar;

import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class calc extends AppCompatActivity {

    private EditText currentInput, voltageInput, panelAreaInput, irradianceInput;
    private Button calculateButton;
    private TextView resultText;
    private String providerName;
    private double providerEfficiency;
    private dataHelper dataHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calc);
        dataHelper = new dataHelper(this);

        providerName = getIntent().getStringExtra(Home.PROVIDER_KEY);
        providerEfficiency = getIntent().getDoubleExtra(Home.EFFICIENCY_KEY, 0.0);
        currentInput = findViewById(R.id.currentInput);
        voltageInput = findViewById(R.id.voltageInput);
        panelAreaInput = findViewById(R.id.panelAreaInput);
        irradianceInput = findViewById(R.id.irradianceInput);
        calculateButton = findViewById(R.id.calculateButton);
        resultText = findViewById(R.id.resultText);
        calculateButton.setOnClickListener(v -> calculateEfficiency());
    }

    private void calculateEfficiency() {
        String currentStr = currentInput.getText().toString().trim();
        String voltageStr = voltageInput.getText().toString().trim();
        String panelAreaStr = panelAreaInput.getText().toString().trim();
        String irradianceStr = irradianceInput.getText().toString().trim();

        if (currentStr.isEmpty() || voltageStr.isEmpty() || panelAreaStr.isEmpty() || irradianceStr.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double current = Double.parseDouble(currentStr);
            double voltage = Double.parseDouble(voltageStr);
            double panelArea = Double.parseDouble(panelAreaStr);
            double irradiance = Double.parseDouble(irradianceStr);

            if(current < 0 || voltage < 0 || panelArea < 0 || irradiance <= 0) {
                Toast.makeText(this, "Invalid input! All values must be positive.", Toast.LENGTH_SHORT).show();
                return;
            }

            double calculatedEfficiency = calculateEfficiencyLogic(current, voltage, panelArea, irradiance);
            if(calculatedEfficiency > 100) {
                Toast.makeText(this, "Calculated efficiency exceeds 100%! Please check your inputs.", Toast.LENGTH_SHORT).show();
                return;
            }

            String resultMessage = "Calculated Efficiency: "+String.format("%.2f",calculatedEfficiency)+"%";
            resultText.setText(resultMessage);
            resultText.setVisibility(TextView.VISIBLE);
            boolean flag = dataHelper.insertData(providerName, current, voltage, panelArea, irradiance, calculatedEfficiency);
            if(!flag) {
                Toast.makeText(this, "Data insertion failed!", Toast.LENGTH_SHORT).show();
            }
            if(calculatedEfficiency < providerEfficiency) showMaintenanceAlert(calculatedEfficiency);
            else Toast.makeText(this, "Your solar panel is performing well!", Toast.LENGTH_SHORT).show();
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid input! Please enter valid numbers.", Toast.LENGTH_SHORT).show();
        }
    }

    private double calculateEfficiencyLogic(double current, double voltage, double panelArea, double irradiance) {
        double outputPower = current * voltage;
        double inputPower = irradiance * panelArea;
        return (outputPower / inputPower) * 100;
    }

    private void showMaintenanceAlert(double efficiency) {
        new AlertDialog.Builder(this)
                .setTitle("Maintenance Required")
                .setMessage("The calculated efficiency (" + String.format("%.2f", efficiency) + "%) is below the recommended level for " + providerName + ".\nPlease consider maintenance!")
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }
}

