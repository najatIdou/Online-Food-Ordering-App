package com.example.fooddeliveryapp.Activity;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fooddeliveryapp.R;
import com.example.fooddeliveryapp.databinding.ActivityLoginBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    // Connect XML UI
    private ActivityLoginBinding binding;

    // Login session
    private SharedPreferences sharedPreferences;

    // Firebase reference
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize ViewBinding
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getWindow().setStatusBarColor(getResources().getColor(R.color.brown_light));
        // Get stored userId
        sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        setVariable();
    }

    // Login or sign up
    private void setVariable() {
        binding.loginBtn.setOnClickListener(v -> {
            // Get user inputs
            String email = binding.editTextEmail.getText().toString().trim();
            String password = binding.editTextPass.getText().toString().trim();

            // Validations
            if (TextUtils.isEmpty(email)) {
                binding.editTextEmail.setError("Email required");
                binding.editTextEmail.requestFocus();
                return;
            }

            if (TextUtils.isEmpty(password)) {
                binding.editTextPass.setError("Password required");
                binding.editTextPass.requestFocus();
                return;
            }

            // Search user by email
            Query emailQuery = databaseReference.orderByChild("email").equalTo(email);

            // Execute query
            emailQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    // If email exist
                    if (snapshot.exists()) {
                        for (DataSnapshot child : snapshot.getChildren()) {
                            // Check password
                            String storedPassword = child.child("password").getValue(String.class);
                            if (storedPassword != null && storedPassword.equals(password)) {
                                // Get user data
                                int userId = Integer.parseInt(child.getKey());
                                String userName = child.child("name").getValue(String.class);
                                String userRole = child.child("role").getValue(String.class);

                                // Save session
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putBoolean("isLoggedIn", true);
                                editor.putInt("userId", userId);
                                editor.putString("userName", userName);
                                editor.putString("userRole", userRole);
                                editor.apply();

                                //Redirect based on role
                                if ("restaurant_owner".equals(userRole)) {
                                    startActivity(new Intent(LoginActivity.this, RestaurantDashboardActivity.class));
                                } else if("customer".equals(userRole)){
                                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                }else if("admin".equals(userRole)){
                                    startActivity(new Intent(LoginActivity.this,AdminDashboardActivity.class));
                                }
                                finish();
                                return;
                            } else {
                                Toast.makeText(LoginActivity.this, "Invalid password", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "Email not registered", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(LoginActivity.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        // Take to sign up activity
        binding.textsignup.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, SignupActivity.class));
            finish();
        });
    }

    // Hide keyboard when outside taps
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) ev.getRawX(), (int) ev.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }
}