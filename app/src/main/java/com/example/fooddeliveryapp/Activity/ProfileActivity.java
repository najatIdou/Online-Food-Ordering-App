package com.example.fooddeliveryapp.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fooddeliveryapp.R;
import com.example.fooddeliveryapp.databinding.ActivityProfileBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {

    // Connect XML UI
    private ActivityProfileBinding binding;

    // Login session
    private SharedPreferences sharedPreferences;

    // Firebase reference
    private DatabaseReference usersRef, addressesRef;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize ViewBinding
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getWindow().setStatusBarColor(getResources().getColor(R.color.brown_light));
        // Get stored userId
        sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);
        userId = sharedPreferences.getInt("userId", -1);

        // If no user
        if (userId == -1) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        // Reference to addresses, users tables in firebase
        usersRef = FirebaseDatabase.getInstance().getReference("users").child(String.valueOf(userId));
        addressesRef = FirebaseDatabase.getInstance().getReference("addresses");


        // Toolbar setup
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Bottom Navigation
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setSelectedItemId(R.id.nav_profile);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                navigateToMain();
                return true;
            } else if (id == R.id.nav_orders) {
                startActivity(new Intent(ProfileActivity.this, OrderActivity.class));
                finish();
                return true;
            } else if (id == R.id.nav_profile) {
                return true;
            }
            return false;
        });

        initViews();
        loadUserData();
        loadAddresses();

        // Add address button
        binding.addAddressBtn.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, AddAddressActivity.class);
            intent.putExtra("TOOLBAR_TITLE", "Add New Address");
            startActivity(intent);
        });

        // Logout button
        findViewById(R.id.rowLogout).setOnClickListener(v -> logout());
    }

    private void initViews() {

        // Set labels
        setRowLabel(R.id.rowName, "Name");
        setRowLabel(R.id.rowEmail, "Email");
        setRowLabel(R.id.rowPhone, "Phone");
        setRowLabel(R.id.rowAddress, "Default Address");
        setRowLabel(R.id.rowLogout, "Logout");

        // Edit profile button
        binding.editPtofileBtn.setOnClickListener(v -> {
            startActivity(new Intent(ProfileActivity.this, EditProfileActivity.class));});
    }

    private void setRowLabel(int includeId, String label) {
        TextView labelView = findViewById(includeId).findViewById(R.id.rowLabel);
        labelView.setText(label);
    }

    private void loadUserData() {
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String name = snapshot.child("name").getValue(String.class);
                    String email = snapshot.child("email").getValue(String.class);
                    String phone = snapshot.child("phone").getValue(String.class);
                    if (phone == null) phone = "Not provided";

                    // Load data from firebase
                    binding.profileName.setText(name);
                    binding.profileEmail.setText(email);
                    binding.rowName.rowValue.setText(name);
                    binding.rowEmail.rowValue.setText(email);
                    binding.rowPhone.rowValue.setText(phone);
                } else {
                    Toast.makeText(ProfileActivity.this, "User not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileActivity.this, "Failed to load user: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadAddresses() {

        // Search address by user id
        Query query = addressesRef.orderByChild("user_id").equalTo(userId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                binding.addressesContainer.removeAllViews();
                // No address found
                if (!snapshot.exists()) {
                    TextView noAddress = new TextView(ProfileActivity.this);
                    noAddress.setText("No saved addresses");
                    noAddress.setTextColor(getColor(android.R.color.darker_gray));
                    noAddress.setPadding(16, 16, 16, 16);
                    binding.addressesContainer.addView(noAddress);
                    binding.rowAddress.rowValue.setText("None");
                    return;
                }

                String defaultAddressText = "";
                for (DataSnapshot child : snapshot.getChildren()) {
                    String addressText = child.child("address_text").getValue(String.class);
                    String label = child.child("label").getValue(String.class);
                    Boolean isDefault = child.child("is_default").getValue(Boolean.class);
                    if (isDefault == null) {
                        isDefault = false;
                    }

                    // Show label + text
                    View itemView = LayoutInflater.from(ProfileActivity.this).inflate(R.layout.item_address, binding.addressesContainer, false);
                    TextView lbl = itemView.findViewById(R.id.addressLabel);
                    TextView txt = itemView.findViewById(R.id.addressText);
                    lbl.setText(label);
                    txt.setText(addressText);

                    // Show default address in profile header
                    if (isDefault) {
                        defaultAddressText = label + ": " + addressText;
                    }

                    Boolean finalIsDefault = isDefault;
                    // Edit address button
                    itemView.findViewById(R.id.editAddress).setOnClickListener(v -> {
                        Intent intent = new Intent(ProfileActivity.this, AddAddressActivity.class);
                        intent.putExtra("TOOLBAR_TITLE", "Edit Address");
                        intent.putExtra("ADDRESS_ID", child.child("id").getValue(Integer.class));
                        intent.putExtra("LABEL", label);
                        intent.putExtra("ADDRESS_TEXT", addressText);
                        intent.putExtra("IS_DEFAULT", finalIsDefault);
                        startActivity(intent);
                    });
                    Integer addressId = child.child("id").getValue(Integer.class);

                    // Delete address button
                    itemView.findViewById(R.id.deleteAddress).setOnClickListener(v -> {

                        new androidx.appcompat.app.AlertDialog.Builder(ProfileActivity.this)
                                .setTitle("Delete Address")
                                .setMessage("Are you sure you want to delete \"" + label + "\"?")
                                .setPositiveButton("Yes", (dialog, which) -> {

                                    if (addressId != null) {
                                        // Delete address
                                        addressesRef.child(String.valueOf(addressId))
                                                .removeValue()
                                                .addOnSuccessListener(aVoid -> {
                                                    Toast.makeText(ProfileActivity.this,
                                                            "Address deleted",
                                                            Toast.LENGTH_SHORT).show();

                                                    // refresh UI
                                                    loadAddresses();
                                                })
                                                .addOnFailureListener(e ->
                                                        Toast.makeText(ProfileActivity.this,
                                                                "Delete failed: " + e.getMessage(),
                                                                Toast.LENGTH_SHORT).show());
                                    }

                                })
                                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                                .setCancelable(true)
                                .show();
                    });
                    binding.addressesContainer.addView(itemView);
                }
                // No default address
                if (defaultAddressText.isEmpty()) {
                    binding.rowAddress.rowValue.setText("None");
                } else {
                    binding.rowAddress.rowValue.setText(defaultAddressText);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileActivity.this, "Failed to load addresses: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Reload addresses and user data
    @Override
    protected void onResume() {
        super.onResume();
        loadAddresses();
        loadUserData();
    }

    // Log out button
    private void logout() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    // Back button
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            navigateToMain();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        navigateToMain();
    }

    private void navigateToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
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