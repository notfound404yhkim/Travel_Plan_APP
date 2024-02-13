package com.example.travelapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import com.example.travelapp.config.Config;

public class SplashActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                navigateToNextActivity();
            }
        }, 2000);
    }

    private void navigateToNextActivity() {
        SharedPreferences sp = getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
        String token = sp.getString("token", "");

        Intent intent;
        if (!token.isEmpty()) {
            // 이미 로그인한 경우 MainActivity로 이동
            intent = new Intent(SplashActivity.this, MainActivity.class);
        } else {
            // 로그인한 적이 없는 경우 LoginActivity로 이동
            intent = new Intent(SplashActivity.this, LoginActivity.class);
        }
        startActivity(intent);
        finish(); // 현재 액티비티 종료
    }
}