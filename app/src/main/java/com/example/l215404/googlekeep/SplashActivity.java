package com.example.l215404.googlekeep;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.l215404.googlekeep.Authentication.SignUpActivity;
import com.example.l215404.googlekeep.HomePage.MainActivity;
import com.example.l215404.googlekeep.SessionManager.SessionManager;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);

        SessionManager sessionManager = new SessionManager(this);


        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent;

            if (sessionManager.isLoggedIn()) {
                intent = new Intent(SplashActivity.this, MainActivity.class);
            } else {
                intent = new Intent(SplashActivity.this, SignUpActivity.class);
            }

            startActivity(intent);
            finish();
        }, 5000);
    }
}