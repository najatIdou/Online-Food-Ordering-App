package com.example.fooddeliveryapp.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fooddeliveryapp.R;
import com.example.fooddeliveryapp.databinding.ActivitySignupBinding;
import com.google.firebase.database.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {

    // Connect XML UI
    private ActivitySignupBinding binding;

    // Firebase reference
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(getResources().getColor(R.color.brown_light));
        // Initialize ViewBinding
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Reference to users table in firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        setVariable();
    }
    private String getCurrentDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }

    // Sign up and login buttons
    private void setVariable() {
        binding.btnSignUp.setOnClickListener(v -> performSignUp());

        binding.textLogin.setOnClickListener(v -> {
            startActivity(new Intent(SignupActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void performSignUp() {
        // Get user inputs
        String fullName = binding.editTextFullName.getText().toString().trim();
        String email = binding.editTextEmail.getText().toString().trim();
        String password = binding.editTextPassword.getText().toString().trim();
        String phone = binding.editTextPhone.getText().toString().trim();

        // Validations
        if (fullName.isEmpty()) {
            binding.editTextFullName.setError("Full name required");
            return;
        }

        if (!isValidEmail(email)) {
            binding.editTextEmail.setError("Enter valid email");
            return;
        }

        if (!isValidPhone(phone)) {
            binding.editTextPhone.setError("Enter valid phone");
            return;
        }

        if (password.isEmpty()) {
            binding.editTextPassword.setError("Password required");
            return;
        }

        int selectedId = binding.radioGroupAccountType.getCheckedRadioButtonId();
        if (selectedId == -1) {
            Toast.makeText(this, "Select account type", Toast.LENGTH_SHORT).show();
            return;
        }
        RadioButton selectedRadio = findViewById(selectedId);
        String accountType = selectedRadio.getText().toString();

        // Search user by email
        databaseReference.orderByChild("email").equalTo(email)
                .addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        // If email exist
                        if (snapshot.exists()) {
                            Toast.makeText(SignupActivity.this,
                                    "Email already registered",
                                    Toast.LENGTH_SHORT).show();
                        } else {

                            // Read all users to generate new Id
                            databaseReference.orderByKey().limitToLast(1)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {

                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                                            int newId = 1;

                                            // Find highest existing Id
                                            if (snapshot.exists()) {
                                                for (DataSnapshot data : snapshot.getChildren()) {
                                                    int lastId = Integer.parseInt(data.getKey());
                                                    newId = lastId + 1;
                                                }
                                            }

                                            // Save new user
                                            registerUserWithId(newId,
                                                    fullName,
                                                    email,
                                                    phone,
                                                    password,
                                                    accountType,
                                                    getCurrentDateTime());
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            Toast.makeText(SignupActivity.this,
                                                    error.getMessage(),
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(SignupActivity.this,
                                error.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Add new user
    private void registerUserWithId(int userId, String fullName, String email, String phone, String password, String accountType, String created_at) {

        // Check user role
        String role = accountType.equalsIgnoreCase("Restaurant Owner")
                ? "restaurant_owner" : "customer";

        // Create user object
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("id", userId);
        userMap.put("name", fullName);
        userMap.put("email", email);
        userMap.put("phone", phone);
        userMap.put("password", password);
        userMap.put("role", role);
        userMap.put("created_at", created_at);

        // Save to firebase
        databaseReference.child(String.valueOf(userId))
                .setValue(userMap)
                .addOnSuccessListener(aVoid -> {Toast.makeText(this, "Signup successful!", Toast.LENGTH_SHORT).show();
                    // If user is owner
                    if (role.equals("restaurant_owner")) {
                        Intent intent = new Intent(this, SetupRestaurantActivity.class);
                        intent.putExtra("ownerId", userId);
                        startActivity(intent);
                    } else {
                        startActivity(new Intent(this, LoginActivity.class));
                    }
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    // Email validation
    private boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email)
                && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    // Phone validation
    private boolean isValidPhone(String phone) {
        return !TextUtils.isEmpty(phone)
                && Patterns.PHONE.matcher(phone).matches();
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