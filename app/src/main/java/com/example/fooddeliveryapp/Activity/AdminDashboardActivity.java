package com.example.fooddeliveryapp.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fooddeliveryapp.Adapter.AdminPagerAdapter;
import com.example.fooddeliveryapp.R;
import com.example.fooddeliveryapp.databinding.ActivityAdminDashboardBinding;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AdminDashboardActivity extends AppCompatActivity {

    // Connect XML UI
    private ActivityAdminDashboardBinding binding;

    // Firebase reference
    private DatabaseReference usersRef, ordersRef;

    // Login session
    private SharedPreferences sharedPreferences;

    // Tab titles
    private String[] titles = {"All Orders", "All Users", "Analytics"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize ViewBinding
        binding = ActivityAdminDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getWindow().setStatusBarColor(getResources().getColor(R.color.brown_light));
        // Check user role
        sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);
        String role = sharedPreferences.getString("userRole", "");

        // Only admin get access
        if (!isLoggedIn || !role.equals("admin")) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        // Reference to user and order tables in firebase
        usersRef = FirebaseDatabase.getInstance().getReference("users");
        ordersRef = FirebaseDatabase.getInstance().getReference("orders");

        // Load statistics
        loadStats();

        // Setup Viewpager
        AdminPagerAdapter adapter = new AdminPagerAdapter(this);
        binding.viewPager.setAdapter(adapter);

        // Connect tabs to pages
        new TabLayoutMediator(binding.tabLayout, binding.viewPager,
                (tab, position) -> tab.setText(titles[position])
        ).attach();

        // Logout button
        binding.btnLogout.setOnClickListener(v -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            // Remove session
            editor.clear();
            editor.apply();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
        loadStats();
    }

    // Load statistics
    private void loadStats() {

        // Count users
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int customers = 0;
                int restaurants = 0;
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    String role = userSnapshot.child("role").getValue(String.class);
                    if ("customer".equals(role)) {
                        customers++;
                    } else if ("restaurant_owner".equals(role)) {
                        restaurants++;
                    }
                }
                binding.tvCustomers.setText("Customers\n" + customers);
                binding.tvRestaurants.setText("Restaurants\n" + restaurants);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        // Count orders and compute pending + revenue
        ordersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int orderCount = (int) snapshot.getChildrenCount();
                binding.tvOrders.setText("Total Orders\n" + orderCount);

                int pendingCount = 0;
                double revenue = 0;

                for (DataSnapshot orderSnapshot : snapshot.getChildren()) {
                    String status = orderSnapshot.child("status").getValue(String.class);
                    if (status == null) continue;

                    // Count pending orders
                    if ("pending".equalsIgnoreCase(status)) {
                        pendingCount++;
                    }

                    // Add to revenue if NOT cancelled or rejected
                    if (!status.equalsIgnoreCase("cancelled") && !status.equalsIgnoreCase("rejected")) {
                        Object totalObj = orderSnapshot.child("total").getValue();
                        double total = 0;
                        if (totalObj instanceof Number) total = ((Number) totalObj).doubleValue();
                        revenue += total;
                    }
                }

                binding.tvPending.setText("Pending\n" + pendingCount);
                binding.tvRevenue.setText("Revenue\n$" + String.format("%.2f", revenue));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    // Back button
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
}