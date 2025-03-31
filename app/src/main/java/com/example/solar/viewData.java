package com.example.solar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class viewData extends AppCompatActivity {

    private dataHelper dataHelper;
    private TableLayout tableLayout;
    private ImageView logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_data);
        logout = findViewById(R.id.logoutIcon);
        tableLayout = findViewById(R.id.tableLayout);
        dataHelper = new dataHelper(this);
        displayAllData();
        logout.setOnClickListener(v -> logoutUser());
    }

    private void displayAllData() {
        Cursor cursor = dataHelper.getAllData();
        tableLayout.removeAllViews();

        TableRow headerRow = new TableRow(this);
        headerRow.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        addHeaderCell(headerRow, "BRAND");
        addHeaderCell(headerRow, "CURR");
        addHeaderCell(headerRow, "VOLT");
        addHeaderCell(headerRow, "AREA");
        addHeaderCell(headerRow, "IRR");
        addHeaderCell(headerRow, "EFF");


        tableLayout.addView(headerRow);

        if (cursor.getCount() == 0) {
            TableRow noDataRow = new TableRow(this);
            TextView noDataText = new TextView(this);
            noDataText.setText("No Data Found!");
            noDataText.setGravity(Gravity.CENTER);
            noDataText.setPadding(4, 2, 0, 3);
            noDataRow.addView(noDataText);
            tableLayout.addView(noDataRow);
        } else {
            while (cursor.moveToNext()) {
                TableRow row = new TableRow(this);
                row.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                addDataCell(row, cursor.getString(0));
                addDataCell(row, cursor.getString(1));
                addDataCell(row, cursor.getString(2));
                addDataCell(row, cursor.getString(3));
                addDataCell(row, cursor.getString(4));
                addDataCell(row, cursor.getString(5));
                tableLayout.addView(row);
            }
        }
    }

    private void logoutUser() {
        Intent intent = new Intent(this, Home.class);
        startActivity(intent);
        finish();
    }

    private void addHeaderCell(TableRow row, String text) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setTypeface(null, Typeface.BOLD);
        textView.setGravity(Gravity.CENTER);
        textView.setPadding(4, 2, 0, 3);
        row.addView(textView);
    }

    private void addDataCell(TableRow row, String text) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setGravity(Gravity.CENTER);
        textView.setPadding(4, 2, 0, 3);
        row.addView(textView);
    }
}

