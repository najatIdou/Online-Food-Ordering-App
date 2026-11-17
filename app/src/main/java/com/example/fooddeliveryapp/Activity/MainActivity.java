package com.example.fooddeliveryapp.Activity;

import static com.google.android.material.internal.ViewUtils.hideKeyboard;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.fooddeliveryapp.Adapter.BestFoodAdapter;
import com.example.fooddeliveryapp.Adapter.CategoryAdapter;
import com.example.fooddeliveryapp.Adapter.FilteredFoodAdapter;
import com.example.fooddeliveryapp.Adapter.RestaurantListAdapter;
import com.example.fooddeliveryapp.Data.Cart;
import com.example.fooddeliveryapp.Data.Data;
import com.example.fooddeliveryapp.Domain.Category;
import com.example.fooddeliveryapp.Domain.Foods;
import com.example.fooddeliveryapp.Domain.Location;
import com.example.fooddeliveryapp.Domain.Restaurant;
import com.example.fooddeliveryapp.R;
import com.example.fooddeliveryapp.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private List<Category> categoryList = new ArrayList<>();
    private List<Foods> bestFoodList = new ArrayList<>();
    private List<Foods> allFoods = new ArrayList<>();
    private Map<Integer, String> durationMap;
    private CategoryAdapter categoryAdapter;
    private BestFoodAdapter bestFoodAdapter;
    private FilteredFoodAdapter filteredFoodAdapter;
    private RestaurantListAdapter restaurantAdapter;
    private Data data;
    private SharedPreferences sharedPreferences;

    // Spinners
    private ArrayAdapter<String> locationAdapter, timeAdapter;
    private ArrayAdapter<String> priceAdapter;
    private List<String> priceRanges;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getWindow().setStatusBarColor(getResources().getColor(R.color.brown_light));
        sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);

        // Check if user is logged in
        if (!sharedPreferences.getBoolean("isLoggedIn", false)) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        String userRole = sharedPreferences.getString("userRole", "");
        if ("restaurant_owner".equals(userRole)) {
            startActivity(new Intent(this, RestaurantDashboardActivity.class));
            finish();
            return;
        }else if ("customer".equals(userRole)) {
            // Set user name in welcome text
            String userName = sharedPreferences.getString("userName", "User");
            binding.userName.setText(userName);
        }else if("admin".equals(userRole)){
            startActivity(new Intent(this, AdminDashboardActivity.class));
            finish();
            return;
        }

        // Logout button
        binding.logoutBtn.setOnClickListener(v -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        });

        // Set up bottom navigation
        BottomNavigationView bottomNav = binding.bottomNav;
        bottomNav.setSelectedItemId(R.id.nav_home);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                return true;
            } else if (id == R.id.nav_orders) {
                startActivity(new Intent(MainActivity.this, OrderActivity.class));
                finish();
                return true;
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                finish();
                return true;
            }
            return false;
        });

        // Initialize views
        binding.bestFoodView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.categoryView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.restaurantRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.filteredFoodRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        categoryAdapter = new CategoryAdapter(this, categoryList);
        bestFoodAdapter = new BestFoodAdapter(this, bestFoodList);
        bestFoodAdapter.setOnCartUpdatedListener(() -> updateCartBadge());
        binding.categoryView.setAdapter(categoryAdapter);
        binding.bestFoodView.setAdapter(bestFoodAdapter);

        // Clear filters button
        binding.clearFiltersBtn.setOnClickListener(v -> {
            binding.locationSp.setSelection(0);
            binding.timeSp.setSelection(0);
            binding.priceSp.setSelection(0);
            showNormalMode();
        });
        // Search button
        binding.searchBtn.setOnClickListener(v -> {
            hideKeyboard(v);
            binding.editTextText.clearFocus();
            performSearch();
        });

        // View all best foods
        binding.AllFoods.setOnClickListener(v -> startActivity(new Intent(this, AllFoodsActivity.class)));

        // View all restaurants
        binding.viewAllRestaurants.setOnClickListener(v -> startActivity(new Intent(this, AllRestaurantsActivity.class)));

        // Cart button
        binding.cartBtn.setOnClickListener(v -> startActivity(new Intent(this, CartActivity.class)));

        // Load data
        data = Data.getInstance();
        data.setDataLoadedListener(new Data.OnDataLoadedListener() {
            @Override
            public void onDataLoaded() {
                runOnUiThread(() -> {
                    // Categories
                    categoryList.clear();
                    categoryList.addAll(data.getCategories());
                    categoryAdapter.notifyDataSetChanged();
                    binding.progressBarCategory.setVisibility(View.GONE);

                    bestFoodList.clear();

                    List<Foods> allFoodsCopy = new ArrayList<>(data.getFoods());

                    //keep only foods with high rating
                    List<Foods> ratedFoods = new ArrayList<>();
                    for (Foods food : allFoodsCopy) {
                        if (food.getStar() != null && food.getStar() > 0) {
                            ratedFoods.add(food);
                        }
                    }

                    // Sort by rating desc
                    ratedFoods.sort((f1, f2) -> Double.compare(f2.getStar(), f1.getStar()));

                    // Add only top 7 rated
                    int limit = Math.min(7, ratedFoods.size());
                    for (int i = 0; i < limit; i++) {
                        bestFoodList.add(ratedFoods.get(i));
                    }

                    bestFoodAdapter.setDurationMap(data.getDurationMap());
                    bestFoodAdapter.notifyDataSetChanged();
                    binding.progressBarBest.setVisibility(View.GONE);

                    // All foods for filtering
                    allFoods = data.getFoods();
                    durationMap = data.getDurationMap();

                    // Restaurants
                    List<Restaurant> allRestaurants = data.getRestaurants();
                    restaurantAdapter = new RestaurantListAdapter(MainActivity.this, allRestaurants, restaurantId -> {
                        Intent intent = new Intent(MainActivity.this, RestaurantHomeActivity.class);
                        intent.putExtra("restaurantId", restaurantId);
                        startActivity(intent);
                    });
                    binding.restaurantRecyclerView.setAdapter(restaurantAdapter);
                    binding.restaurantRecyclerView.setVisibility(View.VISIBLE);
                    binding.progressBarRestaurants.setVisibility(View.GONE);

                    // Setup spinners
                    setupSpinners();

                    // Initialize filtered adapter
                    filteredFoodAdapter = new FilteredFoodAdapter(MainActivity.this, new ArrayList<>(), durationMap);
                    binding.filteredFoodRecyclerView.setAdapter(filteredFoodAdapter);

                    showNormalMode();
                });
            }
        });
        updateCartBadge();
    }

    private void setupSpinners() {
        // Location spinner
        List<Location> locations = data.getLocations();
        List<String> locationNames = new ArrayList<>();
        locationNames.add("All Locations");
        for (Location loc : locations) {
            locationNames.add(loc.getCity() + ", " + loc.getDistrict());
        }
        locationAdapter = new ArrayAdapter<>(this, R.layout.sp_item, locationNames);
        locationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.locationSp.setAdapter(locationAdapter);

        // Time spinner
        List<String> durationValues = new ArrayList<>();
        durationValues.add("All Times");
        durationValues.addAll(durationMap.values());
        timeAdapter = new ArrayAdapter<>(this, R.layout.sp_item, durationValues);
        timeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.timeSp.setAdapter(timeAdapter);

        // Price spinner – generate ranges from actual food prices
        priceRanges = generatePriceRanges(allFoods);
        priceAdapter = new ArrayAdapter<>(this, R.layout.sp_item, priceRanges);
        priceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.priceSp.setAdapter(priceAdapter);

        // Spinner listeners
        AdapterView.OnItemSelectedListener listener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filterFoods();
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        };
        binding.locationSp.setOnItemSelectedListener(listener);
        binding.timeSp.setOnItemSelectedListener(listener);
        binding.priceSp.setOnItemSelectedListener(listener);
    }

    private List<String> generatePriceRanges(List<Foods> foods) {
        List<String> ranges = new ArrayList<>();
        ranges.add("All Prices");
        if (foods.isEmpty()) return ranges;
        double minPrice = Double.MAX_VALUE;
        double maxPrice = Double.MIN_VALUE;
        for (Foods f : foods) {
            double price = f.getPrice();
            if (price < minPrice) minPrice = price;
            if (price > maxPrice) maxPrice = price;
        }
        int start = (int) Math.floor(minPrice / 10) * 10;
        int end = (int) Math.ceil(maxPrice / 10) * 10;
        for (int i = start; i < end; i += 10) {
            ranges.add("$" + i + " - $" + (i + 10));
        }
        if (end < maxPrice) {
            ranges.add("$" + end + "+");
        }
        return ranges;
    }

    private void filterFoods() {
        int locPos = binding.locationSp.getSelectedItemPosition();
        int timePos = binding.timeSp.getSelectedItemPosition();
        int pricePos = binding.priceSp.getSelectedItemPosition();
        if (locPos == 0 && timePos == 0 && pricePos == 0) {
            showNormalMode();
            return;
        }
        List<Foods> filtered = new ArrayList<>();
        for (Foods food : allFoods) {
            boolean matches = true;
            // Time filter
            if (timePos > 0) {
                String selectedDuration = timeAdapter.getItem(timePos);
                int durationId = getDurationIdFromValue(selectedDuration);
                if (food.getDuration_id() != durationId) matches = false;
            }
            // Price filter
            if (pricePos > 0) {
                String selectedRange = priceAdapter.getItem(pricePos);
                double price = food.getPrice();
                if (!priceInRange(price, selectedRange)) matches = false;
            }
            // Location filter
            if (locPos > 0) {
                String selectedLocation = locationAdapter.getItem(locPos);
                Restaurant restaurant = null;
                for (Restaurant r : data.getRestaurants()) {
                    if (r.getId() == food.getRestaurant_id()) {
                        restaurant = r;
                        break;
                    }
                }
                if (restaurant != null) {
                    // Get location
                    Location loc = null;
                    for (Location l : data.getLocations()) {
                        if (l.getId() == restaurant.getLocation_id()) {
                            loc = l;
                            break;
                        }
                    }
                    if (loc != null) {
                        String fullLocation = loc.getCity() + ", " + loc.getDistrict();
                        if (!fullLocation.equals(selectedLocation)) {
                            matches = false;
                        }
                    } else {
                        matches = false;
                    }
                } else {
                    matches = false;
                }
            }

            // Add results
            if (matches) {
                filtered.add(food);
            }
        }
        filteredFoodAdapter.updateList(filtered);
        showFilteredMode();
    }
    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && view != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
    private boolean priceInRange(double price, String range) {
        if (range.equals("All Prices")) return true;
        if (range.endsWith("+")) {
            double min = Double.parseDouble(range.substring(1, range.length() - 1));
            return price >= min;
        }
        String[] parts = range.replace("$", "").split(" - ");
        double min = Double.parseDouble(parts[0]);
        double max = Double.parseDouble(parts[1]);
        return price >= min && price <= max;
    }

    private int getDurationIdFromValue(String value) {
        for (Map.Entry<Integer, String> entry : durationMap.entrySet()) {
            if (entry.getValue().equals(value)) return entry.getKey();
        }
        return -1;
    }

    private void showNormalMode() {
        binding.filteredResultsSection.setVisibility(View.GONE);
        binding.normalContentSection.setVisibility(View.VISIBLE);
    }

    private void showFilteredMode() {
        binding.filteredResultsSection.setVisibility(View.VISIBLE);
        binding.normalContentSection.setVisibility(View.GONE);
    }

    private void performSearch() {
        String query = binding.editTextText.getText().toString().toLowerCase().trim();
        if (query.isEmpty()) {
            filterFoods();
            return;
        }
        List<Foods> results = new ArrayList<>();
        for (Foods food : allFoods) {
            if (food.getName().toLowerCase().contains(query)) {
                results.add(food);
            }
        }
        filteredFoodAdapter.updateList(results);
        showFilteredMode();
    }
    private void updateCartBadge() {
        int totalItems = Cart.getInstance().getTotalItems();

        if (totalItems > 0) {
            binding.cartBadge.setVisibility(View.VISIBLE);
            binding.cartBadge.setText(String.valueOf(totalItems));
        } else {
            binding.cartBadge.setVisibility(View.GONE);
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        updateCartBadge();
    }
    private long backPressedTime;
    @Override
    public void onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            super.onBackPressed();
            return;
        } else {
            Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();
        }
        backPressedTime = System.currentTimeMillis();
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
                    hideKeyboard(v);
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }
}