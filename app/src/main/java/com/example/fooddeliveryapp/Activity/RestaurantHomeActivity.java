package com.example.fooddeliveryapp.Activity;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.bumptech.glide.Glide;
import com.example.fooddeliveryapp.Data.Data;
import com.example.fooddeliveryapp.Domain.Category;
import com.example.fooddeliveryapp.Domain.Foods;
import com.example.fooddeliveryapp.Domain.Location;
import com.example.fooddeliveryapp.Domain.Restaurant;
import com.example.fooddeliveryapp.R;
import com.example.fooddeliveryapp.Fragments.FoodListFragment;
import com.example.fooddeliveryapp.databinding.ActivityRestaurantHomeBinding;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RestaurantHomeActivity extends AppCompatActivity {

    // Connect XML UI
    private ActivityRestaurantHomeBinding binding;
    private int restaurantId;
    private Restaurant restaurant;
    private List<Foods> allFoods;
    private Map<Integer, String> durationMap;

    // Firebase reference
    private DatabaseReference ratingRef;
    private List<Category> categories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize ViewBinding
        binding = ActivityRestaurantHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getWindow().setStatusBarColor(getResources().getColor(R.color.brown_light));
        // Get restaurantId
        restaurantId = getIntent().getIntExtra("restaurantId", -1);

        // Setup toolbar
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("About Restaurant");
        }

        // Load all data
        loadData();
    }

    private void loadData() {
        Data data = Data.getInstance();
        data.setDataLoadedListener(new Data.OnDataLoadedListener() {
            @Override
            public void onDataLoaded() {
                runOnUiThread(() -> {
                    // Find restaurant
                    for (Restaurant r : data.getRestaurants()) {
                        if (r.getId() == restaurantId) {
                            restaurant = r;
                            break;
                        }
                    }

                    // If resatu not found
                    if (restaurant == null) {
                        finish();
                        return;
                    }

                    // Get all data
                    allFoods = data.getFoods();
                    durationMap = data.getDurationMap();
                    categories = data.getCategories();

                    // Set restaurant info
                    binding.restaurantName.setText(restaurant.getName());
                    Data data = Data.getInstance();
                    int locationId = restaurant.getLocation_id();
                    Location loc = null;

                    // Find location
                    for (Location l : data.getLocations()) {
                        if (l.getId() == locationId) {
                            loc = l;
                            break;
                        }
                    }

                    // Show location
                    if (loc != null) {
                        binding.restaurantLocation.setText(loc.getCity()+ " , "+loc.getDistrict()); // or loc.getName(), depending on your Location class
                    } else {
                        binding.restaurantLocation.setText("Unknown Address");
                    }
                    if (restaurantId == -1){
                        return;
                    }

                    // Load rating from firebase
                    ratingRef = FirebaseDatabase.getInstance().getReference("restaurant_ratings");

                    ratingRef.orderByChild("restaurant_id").equalTo(restaurantId)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                    double total = 0;
                                    int count = 0;

                                    // Calculate average rating
                                    for (DataSnapshot s : snapshot.getChildren()) {
                                        Double value = s.child("rating").getValue(Double.class);
                                        if (value != null) {
                                            total += value;
                                            count++;
                                        }
                                    }

                                    float avg = count == 0 ? 0f : (float) (total / count);

                                    binding.ratingBar.setRating(avg);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    binding.ratingBar.setRating(0f);
                                }
                            });

                    // Restaurant status
                    if (restaurant.getIs_open() == 1) {
                        binding.restaurantStatus.setText("OPEN");
                        binding.restaurantStatus.setTextColor(getResources().getColor(R.color.green));
                    } else {
                        binding.restaurantStatus.setText("CLOSED");
                        binding.restaurantStatus.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                    }

                    // Load image
                    Glide.with(RestaurantHomeActivity.this)
                            .load(restaurant.getImage())
                            .placeholder(R.drawable.placeholder)
                            .into(binding.restaurantHeaderImage);

                    // Group foods by category
                    Map<Integer, List<Foods>> foodsByCategory = new HashMap<>();
                    for (Foods food : allFoods) {
                        if (food.getRestaurant_id() == restaurantId) {
                            int catId = food.getCategory_id();
                            if (!foodsByCategory.containsKey(catId)) {
                                foodsByCategory.put(catId, new ArrayList<>());
                            }
                            foodsByCategory.get(catId).add(food);
                        }
                    }

                    // Create fragments for each category that has food
                    List<Fragment> fragments = new ArrayList<>();
                    List<String> tabTitles = new ArrayList<>();
                    for (Category cat : categories) {
                        if (foodsByCategory.containsKey(cat.getId())) {
                            fragments.add(FoodListFragment.newInstance(foodsByCategory.get(cat.getId())));
                            tabTitles.add(cat.getName());
                        }
                    }

                    // Set up Viewpager
                    binding.viewPager.setAdapter(new FragmentStateAdapter(RestaurantHomeActivity.this) {
                        @NonNull
                        @Override
                        public Fragment createFragment(int position) {
                            return fragments.get(position);
                        }

                        @Override
                        public int getItemCount() {
                            return fragments.size();
                        }
                    });

                    // Attach tabs
                    new TabLayoutMediator(binding.tabLayout, binding.viewPager,
                            (tab, position) -> tab.setText(tabTitles.get(position))
                    ).attach();
                });
            }
        });
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