package com.example.fooddeliveryapp.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.fooddeliveryapp.Domain.Order;
import com.example.fooddeliveryapp.Fragments.RestaurantMenuFragment;
import com.example.fooddeliveryapp.Fragments.RestaurantOrdersFragment;
import com.example.fooddeliveryapp.R;
import com.example.fooddeliveryapp.databinding.ActivityRestaurantDashboardBinding;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RestaurantDashboardActivity extends AppCompatActivity implements RestaurantOrdersFragment.OnOrdersChangedListener {

    // Connect XML UI
    private ActivityRestaurantDashboardBinding binding;

    // Current restaurant
    private int restaurantId = -1;
    private DatabaseReference restaurantsRef, ordersRef, foodsRef;
    private RestaurantOrdersFragment ordersFragment;
    private RestaurantMenuFragment menuFragment;

    // For double back press exit
    private long backPressedTime;

    // Firebase reference
    private DatabaseReference restauRef;
    private boolean isOpen = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize ViewBinding
        binding = ActivityRestaurantDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getWindow().setStatusBarColor(getResources().getColor(R.color.brown_light));
        // Get restaurant
        restauRef = FirebaseDatabase.getInstance().getReference("restaurants");

        // Logout button
        binding.btnLogout.setOnClickListener(v -> logout());

        // Get userId
        SharedPreferences prefs = getSharedPreferences("UserData", MODE_PRIVATE);
        int userId = prefs.getInt("userId", -1);

        // If no user
        if (userId == -1) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        // Initialize UI
        updateUI(isOpen);

        // Open or close restau
        binding.switchStatus.setChecked(isOpen);
        binding.switchStatus.setOnCheckedChangeListener((buttonView, isChecked) -> {
            isOpen = isChecked;
            updateUI(isOpen);
            // Save to firebase
            updateRestaurantStatus();
        });
            // Load restaurant info
            fetchRestaurantInfo(userId);
    }

    // Update UI depending on open, closed status
    private void updateUI(boolean isOpen) {

        if (isOpen) {
            binding.tvStatusText.setText("Currently accepting orders");

            binding.statusDot.setBackgroundResource(R.drawable.status_dot_green);

            binding.switchStatus.getTrackDrawable().setTint(getResources().getColor(R.color.green));

        } else {
            binding.tvStatusText.setText("Not accepting orders");

            binding.statusDot.setBackgroundResource(R.drawable.status_dot_gray);

            binding.statusDot.clearAnimation();

            binding.switchStatus.getTrackDrawable().setTint(Color.parseColor("#BDBDBD"));
        }
    }
    private void updateRestaurantStatus() {
        if (restaurantId == -1) return;
        restaurantsRef = FirebaseDatabase.getInstance().getReference("restaurants");

        // Convert bool to int
        int isOpenInt = isOpen ? 1 : 0;

        // Update in firebase
        restaurantsRef.child(String.valueOf(restaurantId))
                .child("is_open")
                .setValue(isOpenInt)
                .addOnSuccessListener(unused ->
                        Toast.makeText(this, "Status updated", Toast.LENGTH_SHORT).show()
                )
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to update", Toast.LENGTH_SHORT).show()
                );
    }

    // Get restau info by owner id
    private void fetchRestaurantInfo(int ownerId) {
        restaurantsRef = FirebaseDatabase.getInstance().getReference("restaurants");
        restaurantsRef.orderByChild("owner_id").equalTo(ownerId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot resSnap : snapshot.getChildren()) {
                                restaurantId = resSnap.child("id").getValue(Integer.class);
                                String name = resSnap.child("name").getValue(String.class);
                                // Get is_open as int
                                Integer isOpenInt = resSnap.child("is_open").getValue(Integer.class);

                                // Convert to bool
                                isOpen = (isOpenInt != null && isOpenInt == 1);
                                binding.tvRestaurantName.setText(name);
                                updateUI(isOpen);

                                binding.switchStatus.setChecked(isOpen);
                                break;
                            }

                            // Set up the ViewPager
                            setupViewPager();
                            loadPendingCount();
                            loadMenuCount();
                        } else {
                            binding.tvRestaurantName.setText("Restaurant not found");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        binding.tvRestaurantName.setText("Error loading");
                    }
                });
    }

    private void setupViewPager() {
        binding.viewPager.setAdapter(new FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                Bundle args = new Bundle();
                args.putInt("restaurantId", restaurantId);

                // Menu or orders tab
                if (position == 0) {
                    ordersFragment = new RestaurantOrdersFragment();
                    ordersFragment.setArguments(args);
                    ordersFragment.setOrdersChangedListener(RestaurantDashboardActivity.this);
                    return ordersFragment;
                } else {
                    menuFragment = new RestaurantMenuFragment();
                    menuFragment.setArguments(args);
                    return menuFragment;
                }
            }

            @Override
            public int getItemCount() {
                return 2;
            }
        });

        new TabLayoutMediator(binding.tabLayout, binding.viewPager,
                (tab, position) -> tab.setText(position == 0 ? "Orders" : "Menu")
        ).attach();
    }

    // Load pending orders count
    private void loadPendingCount() {
        // If no restaurant
        if (restaurantId == -1) {
            return;
        }

        // Reference to orders table in firebase
        ordersRef = FirebaseDatabase.getInstance().getReference("orders");
        ordersRef.orderByChild("restaurant_id").equalTo(restaurantId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int pending = 0;
                        // Count pending orders
                        for (DataSnapshot snap : snapshot.getChildren()) {
                            String status = snap.child("status").getValue(String.class);
                            if (Order.STATUS_PENDING.equals(status)){
                                pending++;
                            }
                        }
                        binding.tvPendingCount.setText(String.valueOf(pending));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        binding.tvPendingCount.setText("0");
                    }
                });
    }

    // Load menu food count
    private void loadMenuCount() {
        if (restaurantId == -1) return;

        // Reference to food table in firebase
        foodsRef = FirebaseDatabase.getInstance().getReference("foods");
        foodsRef.orderByChild("restaurant_id").equalTo(restaurantId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        // Count menu items
                        binding.tvMenuCount.setText(String.valueOf(snapshot.getChildrenCount()));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        binding.tvMenuCount.setText("0");
                    }
                });
    }

    @Override
    public void onOrdersChanged() {

        // refresh pending count when orders are accepted or rejected
        loadPendingCount();
    }

    // Logout button
    private void logout() {
        SharedPreferences.Editor editor = getSharedPreferences("UserData", MODE_PRIVATE).edit();
        editor.clear();
        editor.apply();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

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
}