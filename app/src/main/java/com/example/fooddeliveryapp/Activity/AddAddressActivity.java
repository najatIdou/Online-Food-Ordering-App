package com.example.fooddeliveryapp.Activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fooddeliveryapp.R;
import com.example.fooddeliveryapp.databinding.ActivityAddAddressBinding;
import com.google.firebase.database.*;

import java.util.HashMap;
import java.util.Map;

public class AddAddressActivity extends AppCompatActivity {

    // Connect XML UI
    private ActivityAddAddressBinding binding;

    // Firebase reference
    private DatabaseReference addressesRef;

    // Login session
    private SharedPreferences sharedPreferences;

    // Ids
    private int userId;
    private int addressId = -1;
    private boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize ViewBinding
        binding = ActivityAddAddressBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getWindow().setStatusBarColor(getResources().getColor(R.color.brown_light));
        // Toolbar setup
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            String title = getIntent().getStringExtra("TOOLBAR_TITLE");

            if (title == null) {
                title = "Add New Address";
            }

            getSupportActionBar().setTitle(title);

            // Show back button
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Back button
        binding.toolbar.setNavigationOnClickListener(v -> finish());

        // Get stored userId
        sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);
        userId = sharedPreferences.getInt("userId", -1);

        // If no user
        if (userId == -1) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Reference to addresses table in firebase
        addressesRef = FirebaseDatabase.getInstance().getReference("addresses");

        // Save button
        binding.btnSaveAddress.setOnClickListener(v -> saveAddress());

        // Check if user from edit
        addressId = getIntent().getIntExtra("ADDRESS_ID", -1);

        // Load existing address
        if (addressId != -1) {
            isEditMode = true;
            binding.editTextLabel.setText(getIntent().getStringExtra("LABEL"));
            binding.editTextAddress.setText(getIntent().getStringExtra("ADDRESS_TEXT"));
            binding.checkBoxDefault.setChecked(getIntent().getBooleanExtra("IS_DEFAULT", false));
            binding.btnSaveAddress.setText("Update Address");
        }
    }

    // Save Address
    private void saveAddress() {

        // Get user inputs
        String label = binding.editTextLabel.getText().toString().trim();
        String addressText = binding.editTextAddress.getText().toString().trim();

        // Validations
        if (TextUtils.isEmpty(label)) {
            binding.editTextLabel.setError("Label required");
            binding.editTextLabel.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(addressText)) {
            binding.editTextAddress.setError("Address required");
            binding.editTextAddress.requestFocus();
            return;
        }

        boolean setAsDefault = binding.checkBoxDefault.isChecked();

        // Decide create or update
        if (isEditMode) {
            checkOwnershipAndUpdate(addressId, label, addressText, setAsDefault);
        } else {
            createNewAddress(label, addressText, setAsDefault);
        }
    }

    // Verify the user before updating
    private void checkOwnershipAndUpdate(int id, String label, String addressText, boolean isDefault) {
        addressesRef.child(String.valueOf(id)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    Toast.makeText(AddAddressActivity.this, "Address not found", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }
                Integer ownerId = snapshot.child("user_id").getValue(Integer.class);
                if (ownerId == null || ownerId != userId) {
                    finish();
                    return;
                }
                // Proceed with update
                updateExistingAddress(id, label, addressText, isDefault);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AddAddressActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateExistingAddress(int id, String label, String addressText, boolean isDefault) {

        // If set address as default
        if (isDefault) {

            // Remove default from other addresses of user
            addressesRef.orderByChild("user_id").equalTo(userId)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot child : snapshot.getChildren()) {
                                String childId = child.getKey();
                                if (childId != null && !childId.equals(String.valueOf(id))) {
                                    child.getRef().child("is_default").setValue(false);
                                }
                            }
                            // Updating address
                            performUpdate(id, label, addressText, true);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {}
                    });
        } else {
            // Update non default
            performUpdate(id, label, addressText, false);
        }
    }

    // Update address
    private void performUpdate(int id, String label, String addressText, boolean isDefault) {

        // Create address object
        Map<String, Object> address = new HashMap<>();
        address.put("label", label);
        address.put("address_text", addressText);
        address.put("is_default", isDefault);

        // Update in firebase
        addressesRef.child(String.valueOf(id)).updateChildren(address)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Address updated", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Update failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    // Create New Address
    private void createNewAddress(String label, String addressText, boolean isDefault) {

        // Read all addresses to generate new Id
        addressesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int maxId = 0;

                // Find highest existing Id
                for (DataSnapshot child : snapshot.getChildren()) {
                    Integer id = child.child("id").getValue(Integer.class);
                    if (id != null && id > maxId) {
                        maxId = id;
                    }
                }

                int newId = maxId + 1;

                // If set address as default
                if (isDefault) {
                    // Remove default from other addresses of user
                    addressesRef.orderByChild("user_id").equalTo(userId)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot child : snapshot.getChildren()) {
                                        child.getRef().child("is_default").setValue(false);
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {}
                            });
                }

                // Create address object
                Map<String, Object> addressMap = new HashMap<>();
                addressMap.put("id", newId);
                addressMap.put("user_id", userId);
                addressMap.put("label", label);
                addressMap.put("address_text", addressText);
                addressMap.put("is_default", isDefault);

                // Save to firebase
                addressesRef.child(String.valueOf(newId)).setValue(addressMap)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(AddAddressActivity.this, "Address saved", Toast.LENGTH_SHORT).show();
                            finish();
                        })
                        .addOnFailureListener(e ->
                                Toast.makeText(AddAddressActivity.this, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AddAddressActivity.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull android.view.MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}