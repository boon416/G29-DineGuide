package com.example.dineguide;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UserActivity extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseUser user;
    TextView userDetails;
    Button btnSpinwheel2, btnHistory, btnBrowseRestaurants, btnViewFavorites, btnLogoutNow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        userDetails = findViewById(R.id.user_details);
        btnSpinwheel2 = findViewById(R.id.btnSpinwheel2);
        btnHistory = findViewById(R.id.btnHistory);
        btnBrowseRestaurants = findViewById(R.id.btnBrowseRestaurants);
        btnViewFavorites = findViewById(R.id.btnViewFavorites);
        btnLogoutNow = findViewById(R.id.btnLogoutNow);

        if (user == null) {
            // 如果没登录，跳回MainActivity
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        } else {
            // 设定欢迎文字
            userDetails.setText("Hi, " + user.getEmail());
        }

        btnSpinwheel2.setOnClickListener(v -> {
            Intent intent = new Intent(UserActivity.this, SpinActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        btnHistory.setOnClickListener(v -> {
            startActivity(new Intent(UserActivity.this, HistoryActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        btnBrowseRestaurants.setOnClickListener(v -> {
            startActivity(new Intent(UserActivity.this, BrowseActivity.class));
        });

        btnViewFavorites.setOnClickListener(v -> {
            startActivity(new Intent(UserActivity.this, FavoriteActivity.class));
        });

        btnLogoutNow.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        });
    }
}
