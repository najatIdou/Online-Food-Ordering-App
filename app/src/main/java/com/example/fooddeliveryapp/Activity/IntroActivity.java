package com.example.fooddeliveryapp.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fooddeliveryapp.databinding.ActivityIntroBinding;

public class IntroActivity extends AppCompatActivity {

    // Connect XML UI
    private ActivityIntroBinding binding;

    // Login session
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize ViewBinding
        binding = ActivityIntroBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setVariable();
        getWindow().setStatusBarColor(Color.parseColor("#ffffff"));

        // Get stored userId
        sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);
        if (isUserLoggedIn()) {
            goToMain();
            return;
        }
    }

    // If user logged in
    private boolean isUserLoggedIn() {
        return sharedPreferences.getBoolean("isLoggedIn", false);
    }

    // Login or sign up
    private void setVariable() {
        binding.loginBtn.setOnClickListener(v ->
                startActivity(new Intent(this, LoginActivity.class))
        );

        binding.signupBtn.setOnClickListener(v ->
                startActivity(new Intent(this, SignupActivity.class))
        );
    }

    // Go to main activity
    private void goToMain() {

        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

}