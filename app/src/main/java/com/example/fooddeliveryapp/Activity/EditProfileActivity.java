package com.example.fooddeliveryapp.Activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fooddeliveryapp.R;
import com.example.fooddeliveryapp.databinding.ActivityEditProfileBinding;
import com.google.firebase.database.*;

import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {

    // Connect XML UI
    private ActivityEditProfileBinding binding;
    int userId = -1;

    // Firebase reference
    private DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize ViewBinding
        binding = ActivityEditProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getWindow().setStatusBarColor(getResources().getColor(R.color.brown_light));
        // Get logged user Id
        SharedPreferences sp = getSharedPreferences("UserData", MODE_PRIVATE);
        userId = sp.getInt("userId", -1);

        if (userId == -1) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Reference to users table in firebase
        userRef = FirebaseDatabase.getInstance().getReference("users").child(String.valueOf(userId));

        // Toolbar setup
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Load user data into fields
        loadUserData();

        // Save button
        binding.saveBtn.setOnClickListener(v -> saveProfile());
    }

    // Load data
    private void loadUserData() {

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {

                    // Load user data
                    String userName = snapshot.child("name").getValue(String.class);
                    String userEmail = snapshot.child("email").getValue(String.class);
                    String userPhone = snapshot.child("phone").getValue(String.class);
                    if (userName != null) binding.NameEdit.setText(userName);
                    if (userEmail != null) binding.EmailEdit.setText(userEmail);
                    if (userPhone != null) binding.PhoneEdit.setText(userPhone);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(EditProfileActivity.this,
                        "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Save profile
    private void saveProfile() {

        // Get user inputs
        String newName = binding.NameEdit.getText().toString().trim();
        String newEmail = binding.EmailEdit.getText().toString().trim();
        String newPhone = binding.PhoneEdit.getText().toString().trim();

        // Validations
        if (newName.isEmpty()) {
            binding.NameEdit.setError("Name required");
            return;
        }

        if (newEmail.isEmpty()) {
            binding.EmailEdit.setError("Email required");
            return;
        }

        if (newPhone.isEmpty()) {
            binding.PhoneEdit.setError("Phone required");
            return;
        }

        // Create user object
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("name", newName);
        userMap.put("email", newEmail);
        userMap.put("phone", newPhone);

        // Save to firebase
        userRef.updateChildren(userMap)
                .addOnSuccessListener(a -> {
                    Toast.makeText(this, "Profile updated!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show();
                });
    }

    // Back button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}