package com.example.fooddeliveryapp.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.fooddeliveryapp.Adapter.OrderAdapter;
import com.example.fooddeliveryapp.Data.Data;
import com.example.fooddeliveryapp.Domain.Order;
import com.example.fooddeliveryapp.R;
import com.example.fooddeliveryapp.databinding.ActivityOrderBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

public class OrderActivity extends AppCompatActivity {

    // Connect XML UI
    private ActivityOrderBinding binding;

    // RecyclerView adapter
    private OrderAdapter adapter;
    private Data data;

    // Login session
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize ViewBinding
        binding = ActivityOrderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getWindow().setStatusBarColor(getResources().getColor(R.color.brown_light));
        // Get stored userId
        sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);

        // Check login
        if (!sharedPreferences.getBoolean("isLoggedIn", false)) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        // Toolbar setup
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("My Orders");
        }

        // Recyclerview setup
        binding.ordersRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Bottom Navigation
        BottomNavigationView bottomNav = binding.bottomNav;
        bottomNav.setSelectedItemId(R.id.nav_orders);

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                startActivity(new Intent(this, MainActivity.class));
                finish();
                return true;
            }

            if (id == R.id.nav_orders) {
                return true;
            }

            if (id == R.id.nav_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
                finish();
                return true;
            }

            return false;
        });
    }

    // Refreshing page
    @Override
    protected void onResume() {
        super.onResume();
        loadOrders();
    }

    private void loadOrders() {
        // Get data instance
        data = Data.getInstance();

        // If no user
        int userId = sharedPreferences.getInt("userId", -1);
        if (userId == -1) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }
        // Reload data from firebase
        data.reloadOrders(() -> runOnUiThread(() -> {
            List<Order> orders = data.getOrdersForUser(userId);
            adapter.updateOrders(orders);
            adapter.notifyDataSetChanged();
        }));

        // Load data
        data.setDataLoadedListener(() -> runOnUiThread(() -> {

            // Get user's orders
            List<Order> orders = data.getOrdersForUser(userId);

            if (orders == null || orders.isEmpty()) {
                Toast.makeText(this, "No orders found", Toast.LENGTH_SHORT).show();
            }

            if (adapter == null) {
                adapter = new OrderAdapter(this, orders);
                binding.ordersRecyclerView.setAdapter(adapter);
            } else {
                adapter.updateOrders(orders);
                adapter.notifyDataSetChanged();
            }

            // Load ratings
            data.loadUserRatings(userId, () ->
                    runOnUiThread(() -> adapter.notifyDataSetChanged())
            );

        }));
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
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}