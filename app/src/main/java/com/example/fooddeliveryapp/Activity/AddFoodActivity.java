package com.example.fooddeliveryapp.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fooddeliveryapp.Domain.Category;
import com.example.fooddeliveryapp.Domain.Foods;
import com.example.fooddeliveryapp.Domain.Time;
import com.example.fooddeliveryapp.R;
import com.example.fooddeliveryapp.databinding.ActivityAddFoodBinding;
import com.google.firebase.database.*;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddFoodActivity extends AppCompatActivity {

    // Connect XML UI
    private ActivityAddFoodBinding binding;

    // Store image as Base64 string
    private String encodedImage = "";

    // Selected values from spinners
    private int selectedCategoryId = -1;
    private int selectedDurationId = -1;

    // Firebase reference
    private DatabaseReference foodsRef, durationRef, categoryRef, restaurantsRef;

    // Login session
    private SharedPreferences prefs;
    private boolean isEditMode = false;

    // Ids
    private int userId, foodId = -1, restaurantId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize ViewBinding
        binding = ActivityAddFoodBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getWindow().setStatusBarColor(getResources().getColor(R.color.brown_light));
        // Hide image preview
        binding.imgFoodPreview.setVisibility(View.GONE);

        // Get foodId
        foodId = getIntent().getIntExtra("foodId", -1);

        // Toolbar setup
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);


            if (foodId != -1) {
                // Editing existing food
                isEditMode = true;
                binding.btnAdd.setText("Update Item");
                getSupportActionBar().setTitle("Edit Food Item");
            } else {
                // Adding new food
                binding.btnAdd.setText("Add Item");
                getSupportActionBar().setTitle("Add New Food Item");
            }
        }

        // Reference to food table in firebase
        foodsRef = FirebaseDatabase.getInstance().getReference("foods");

        //Load dropdown data
        loadCategories();
        loadDurations();

        // Pick image
        binding.btnPickImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, 100);
        });

        // Get restaurant Id
        prefs = getSharedPreferences("UserData", MODE_PRIVATE);
        userId = prefs.getInt("userId", -1);

        // If no user
        if (userId == -1) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        // Get restaurant id
        fetchRestaurantId(userId);

        // Load existing food
        if (foodId != -1) {
            loadFoodData(foodId);
        }

        // Add button
        binding.btnAdd.setOnClickListener(v -> {
            if (isEditMode){
                updateFood();
            }
            else{
                saveFood();
            }
        });

        // Cancel button
        binding.btnCancel.setOnClickListener(v -> finish());
    }

    // Load durations
    private void loadDurations() {

        // Reference to durations table in firebase
        durationRef = FirebaseDatabase.getInstance().getReference("durations");

        durationRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                List<Time> durationList = new ArrayList<>();

                for (DataSnapshot child : snapshot.getChildren()) {
                    Time duration = child.getValue(Time.class);
                    if (duration != null) durationList.add(duration);
                }

                ArrayAdapter<Time> adapter =
                        new ArrayAdapter<>(AddFoodActivity.this,
                                android.R.layout.simple_spinner_item,
                                durationList);

                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                binding.spinnerDuration.setAdapter(adapter);

                // Set selected duration if editing
                if (selectedDurationId != -1) {
                    for (int i = 0; i < durationList.size(); i++) {
                        if (durationList.get(i).getId() == selectedDurationId) {
                            binding.spinnerDuration.setSelection(i);
                            break;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AddFoodActivity.this, "Failed to load categories", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Load categories
    private void loadCategories() {

        // Reference to categories table in firebase
        categoryRef = FirebaseDatabase.getInstance().getReference("categories");

        categoryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                List<Category> categoryList = new ArrayList<>();

                for (DataSnapshot child : snapshot.getChildren()) {
                    Category category = child.getValue(Category.class);
                    if (category != null) categoryList.add(category);
                }

                ArrayAdapter<Category> adapter =
                        new ArrayAdapter<>(AddFoodActivity.this,
                                android.R.layout.simple_spinner_item,
                                categoryList);

                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                binding.spinnerCategory.setAdapter(adapter);

                // Set selected category if editing
                if (selectedCategoryId != -1) {
                    for (int i = 0; i < categoryList.size(); i++) {
                        if (categoryList.get(i).getId() == selectedCategoryId) {
                            binding.spinnerCategory.setSelection(i);
                            break;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AddFoodActivity.this, "Failed to load categories", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Fetch restaurants
    private void fetchRestaurantId(int ownerId) {

        // Reference to restaurants table in firebase
        restaurantsRef = FirebaseDatabase.getInstance().getReference("restaurants");

        restaurantsRef.orderByChild("owner_id").equalTo(ownerId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot resSnap : snapshot.getChildren()) {
                                // Finds restaurant of logged in owner
                                restaurantId = resSnap.child("id").getValue(Integer.class);
                                break;
                            }
                        } else {
                            Toast.makeText(AddFoodActivity.this, "Restaurant not found", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(AddFoodActivity.this, "Database error", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
    }

    // Save food
    private void saveFood() {

        // Get owner inputs
        String name = binding.etFoodName.getText().toString().trim();
        String desc = binding.etDescription.getText().toString().trim();
        String priceStr = binding.etPrice.getText().toString().trim();
        Category selectedCat = (Category) binding.spinnerCategory.getSelectedItem();
        Time selectedDur = (Time) binding.spinnerDuration.getSelectedItem();

        // Validations
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(desc) || TextUtils.isEmpty(priceStr)) {
            Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(encodedImage)) {
            Toast.makeText(this, "Select an image", Toast.LENGTH_SHORT).show();
            return;
        }

        // Convert price
        double price;
        try {
            price = Double.parseDouble(priceStr);
        } catch (Exception e) {
            binding.etPrice.setError("Invalid number");
            return;
        }

        // Read all foods to generate new Id
        foodsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                    int maxId = 0;

                    // Find highest existing Id
                for (DataSnapshot child : snapshot.getChildren()) {
                    Integer id = child.child("id").getValue(Integer.class);
                    if (id != null && id > maxId)
                        maxId = id;
                }

                int newId = maxId + 1;

                // Create food object
                Foods food = new Foods();
                food.setId(newId);
                food.setName(name);
                food.setDescription(desc);
                food.setPrice(price);
                food.setImage_path(encodedImage);
                food.setCategory_id(selectedCat.getId());
                food.setDuration_id(selectedDur.getId());
                food.setRestaurant_id(restaurantId);

                // Save to firebase
                foodsRef.child(String.valueOf(newId)).setValue(food)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(AddFoodActivity.this, "Food added", Toast.LENGTH_SHORT).show();
                            finish();
                        });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    // Load food
    private void loadFoodData(int foodId) {
        foodsRef.child(String.valueOf(foodId)).addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()) return;

                        Foods food = snapshot.getValue(Foods.class);

                        // Check if food exists
                        if (food == null) return;

                        // Load existing food
                        binding.etFoodName.setText(food.getName());
                        binding.etDescription.setText(food.getDescription());
                        binding.etPrice.setText(String.valueOf(food.getPrice()));
                        selectedCategoryId = food.getCategory_id();
                        selectedDurationId = food.getDuration_id();
                        restaurantId = food.getRestaurant_id();
                        encodedImage = food.getImage_path();

                        // Load image
                        if (encodedImage != null && encodedImage.startsWith("data:image")) {
                            try {
                                String base64 = encodedImage.substring(encodedImage.indexOf(",") + 1);
                                byte[] bytes = Base64.decode(base64, Base64.DEFAULT);
                                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                binding.imgFoodPreview.setImageBitmap(bitmap);
                                binding.imgFoodPreview.setVisibility(View.VISIBLE);
                            } catch (Exception e) {
                                binding.imgFoodPreview.setImageResource(R.drawable.placeholder);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    // Update food
    private void updateFood() {

        // Get owner inputs
        String name = binding.etFoodName.getText().toString().trim();
        String desc = binding.etDescription.getText().toString().trim();
        String priceStr = binding.etPrice.getText().toString().trim();
        Category selectedCat = (Category) binding.spinnerCategory.getSelectedItem();
        Time selectedDur = (Time) binding.spinnerDuration.getSelectedItem();

        // Validations
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(desc) || TextUtils.isEmpty(priceStr)) {
            Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Convert price
        double price;
        try {
            price = Double.parseDouble(priceStr);
        } catch (Exception e) {
            binding.etPrice.setError("Invalid number");
            return;
        }

        // Update food object
        Map<String, Object> updates = new HashMap<>();
        updates.put("name", name);
        updates.put("description", desc);
        updates.put("price", price);
        updates.put("category_id", selectedCat.getId());
        updates.put("duration_id", selectedDur.getId());
        updates.put("restaurant_id", restaurantId);

        // Update image if changed
        if (encodedImage != null && !encodedImage.isEmpty()) {
            updates.put("image_path", encodedImage);
        }

        // Save to firebase
        foodsRef.child(String.valueOf(foodId)).updateChildren(updates)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Updated", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Update failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    // Pick image
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            try {
                InputStream inputStream = getContentResolver().openInputStream(data.getData());
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                binding.imgFoodPreview.setImageBitmap(bitmap);
                binding.imgFoodPreview.setVisibility(View.VISIBLE);

                encodedImage = encodeImage(bitmap);

            } catch (Exception e) {
                Toast.makeText(this, "Image error", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Convert image to base64
    private String encodeImage(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
        byte[] bytes = baos.toByteArray();

        return "data:image/jpeg;base64," + Base64.encodeToString(bytes, Base64.NO_WRAP);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}