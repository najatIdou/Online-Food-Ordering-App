package com.example.fooddeliveryapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.fooddeliveryapp.Adapter.AllRestaurantsAdapter;
import com.example.fooddeliveryapp.Data.Data;
import com.example.fooddeliveryapp.Domain.Restaurant;
import com.example.fooddeliveryapp.R;
import com.example.fooddeliveryapp.databinding.ActivityAllRestaurantsBinding;

import java.util.List;

public class AllRestaurantsActivity extends AppCompatActivity {

    // Connect XML UI
    private ActivityAllRestaurantsBinding binding;

    // RecyclerView adapter
    private AllRestaurantsAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize ViewBinding
        binding = ActivityAllRestaurantsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getWindow().setStatusBarColor(getResources().getColor(R.color.brown_light));
        // Recyclerview setup
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Load restaurants
        loadRestaurants();

        // Toolbar setup
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void loadRestaurants() {
        List<Restaurant> restaurants = Data.getInstance().getRestaurants();

        // Attach adapter to recyclerview
        adapter = new AllRestaurantsAdapter(this, restaurants, restaurantId -> {
            Intent intent = new Intent(AllRestaurantsActivity.this, RestaurantHomeActivity.class);
            startActivity(intent);
        });

        binding.recyclerView.setAdapter(adapter);
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