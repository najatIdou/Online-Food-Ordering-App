package com.example.fooddeliveryapp.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fooddeliveryapp.R;
import com.example.fooddeliveryapp.databinding.ActivitySetupRestaurantBinding;
import com.google.firebase.database.*;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class SetupRestaurantActivity extends AppCompatActivity {

    // Connect XML UI
    private ActivitySetupRestaurantBinding binding;
    private String encodedImage = null;

    // Firebase reference
    private DatabaseReference restaurantsRef;
    private DatabaseReference locationsRef;

    private int ownerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize ViewBinding
        binding = ActivitySetupRestaurantBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getWindow().setStatusBarColor(getResources().getColor(R.color.brown_light));
        // Get ownerId
        ownerId = getIntent().getIntExtra("ownerId", -1);

        // Pick image
        binding.btnPickImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, 100);
        });

        // Complete setup button
        binding.btnComplete.setOnClickListener(v -> saveRestaurant());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            try {

                // Get the URI of the selected image
                Uri uri = data.getData();

                // Open the image as an InputStream
                InputStream inputStream = getContentResolver().openInputStream(uri);

                // Convert InputStream
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                // Show preview of selected image
                binding.imgPreview.setImageBitmap(bitmap);
                binding.imgPreview.setVisibility(View.VISIBLE);

                // Convert to base64
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                byte[] bytes = baos.toByteArray();

                encodedImage = "data:image/jpeg;base64," +
                        Base64.encodeToString(bytes, Base64.DEFAULT);

            } catch (Exception e) {
                Toast.makeText(this, "Image error", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void saveRestaurant() {

        // Get user inputs
        String name = binding.etRestaurantName.getText().toString().trim();
        String city = binding.etCity.getText().toString().trim();
        String district = binding.etDistrict.getText().toString().trim();

        // Validations
        if (name.isEmpty() || city.isEmpty() || district.isEmpty()) {
            Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        // Check image
        if (encodedImage == null || encodedImage.isEmpty()) {
            Toast.makeText(this, "Please select a restaurant image", Toast.LENGTH_SHORT).show();
            return;
        }

        // Reference to location table in firebase
        locationsRef = FirebaseDatabase.getInstance().getReference("locations");

        // Read all locations to generate new Id
        locationsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                // Find highest existing Id
                int maxId = 0;
                for (DataSnapshot child : snapshot.getChildren()) {
                    Integer id = child.child("id").getValue(Integer.class);
                    if (id != null && id > maxId) maxId = id;
                }
                int newLocationId = maxId + 1;

                // Create location object
                Map<String, Object> locationMap = new HashMap<>();
                locationMap.put("id", newLocationId);
                locationMap.put("city", city);
                locationMap.put("district", district);

                locationsRef.child(String.valueOf(newLocationId))
                        .setValue(locationMap);

                // Create restauant
                createRestaurant(newLocationId, name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void createRestaurant(int locationId, String name) {

        // Reference to restaurant table in firebase
        restaurantsRef = FirebaseDatabase.getInstance().getReference("restaurants");

        // Read all restaurants to generate new Id
        restaurantsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                // Find highest existing Id
                int maxId = 0;
                for (DataSnapshot child : snapshot.getChildren()) {
                    Integer id = child.child("id").getValue(Integer.class);
                    if (id != null && id > maxId) maxId = id;
                }

                int newId = maxId + 1;

                // Create restaurant object
                Map<String, Object> restaurantMap = new HashMap<>();
                restaurantMap.put("id", newId);
                restaurantMap.put("name", name);
                restaurantMap.put("owner_id", ownerId);
                restaurantMap.put("location_id", locationId);
                restaurantMap.put("is_open", 1);
                restaurantMap.put("image", encodedImage);

                // Save to firebase
                restaurantsRef.child(String.valueOf(newId))
                        .setValue(restaurantMap)
                        .addOnSuccessListener(unused -> {

                            Toast.makeText(SetupRestaurantActivity.this,
                                    "Setup Complete!", Toast.LENGTH_SHORT).show();

                            startActivity(new Intent(
                                    SetupRestaurantActivity.this,
                                    RestaurantDashboardActivity.class));

                            finish();
                        });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}