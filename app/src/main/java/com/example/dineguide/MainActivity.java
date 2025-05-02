package com.example.dineguide;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.example.dineguide.HistoryActivity;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    Button btnSpinwheel, btnBrowse, btnLoginNow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnSpinwheel = findViewById(R.id.btnSpinwheel);
        btnBrowse = findViewById(R.id.btnBrowse);
        btnLoginNow = findViewById(R.id.btnLoginNow);

        // ✨ Start Spinwheel 按钮
        btnSpinwheel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 打开 Spinwheel Activity
                Intent intent = new Intent(MainActivity.this, SpinActivity.class);
                startActivity(intent);
            }
        });

        // ✨ Browse Restaurants 按钮
        btnBrowse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, BrowseActivity.class);
                startActivity(intent);
            }
        });

        // ✨ Login Now 按钮
        btnLoginNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 打开 Login Activity
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        Button btnHistory = findViewById(R.id.btnHistoryMain);
        btnHistory.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, HistoryActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

    }
}
