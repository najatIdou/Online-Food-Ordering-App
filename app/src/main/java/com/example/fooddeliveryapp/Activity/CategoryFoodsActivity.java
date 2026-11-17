package com.example.fooddeliveryapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.fooddeliveryapp.Adapter.FoodAdapter;
import com.example.fooddeliveryapp.Data.Data;
import com.example.fooddeliveryapp.Domain.Category;
import com.example.fooddeliveryapp.Domain.Foods;
import com.example.fooddeliveryapp.R;
import com.example.fooddeliveryapp.databinding.ActivityCategoryFoodsBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CategoryFoodsActivity extends AppCompatActivity {

    // Connect XML UI
    private ActivityCategoryFoodsBinding binding;

    // RecyclerView adapter
    private FoodAdapter adapter;
    private int categoryId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize ViewBinding
        binding = ActivityCategoryFoodsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getWindow().setStatusBarColor(getResources().getColor(R.color.brown_light));
        // Get category Id from intent
        categoryId = getIntent().getIntExtra("categoryId", -1);

        // Setup toolbar
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        // Set category title
        Data data = Data.getInstance();
        String categoryName = "";
        for (Category cat : data.getCategories()) {
            if (cat.getId() == categoryId) {
                categoryName = cat.getName();
                break;
            }
        }
        binding.categoryTitle.setText(categoryName);

        // Filter foods by category
        List<Foods> foodsInCategory = new ArrayList<>();
        for (Foods f : data.getFoods()) {
            if (f.getCategory_id() == categoryId) {
                foodsInCategory.add(f);
            }
        }

        // Get duration map
        Map<Integer, String> durationMap = data.getDurationMap();

        // Setup Recyclerview
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Adapter with click listener to food details
        adapter = new FoodAdapter(this, foodsInCategory, food -> {
            Intent intent = new Intent(CategoryFoodsActivity.this, DishDetailActivity.class);
            intent.putExtra("foodId", food.getId());
            startActivity(intent);
        });
        // Duration map to adapter
        adapter.setDurationMap(durationMap);

        binding.recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}