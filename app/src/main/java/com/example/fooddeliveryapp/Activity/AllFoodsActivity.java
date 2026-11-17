package com.example.fooddeliveryapp.Activity;

import android.os.Bundle;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.fooddeliveryapp.Adapter.FilteredFoodAdapter;
import com.example.fooddeliveryapp.Data.Data;
import com.example.fooddeliveryapp.Domain.Foods;
import com.example.fooddeliveryapp.R;
import com.example.fooddeliveryapp.databinding.ActivityAllFoodsBinding;

import java.util.List;

public class AllFoodsActivity extends AppCompatActivity {

    // Connect XML UI
    private ActivityAllFoodsBinding binding;

    // RecyclerView adapter
    private FilteredFoodAdapter adapter;
    private Data data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize ViewBinding
        binding = ActivityAllFoodsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getWindow().setStatusBarColor(getResources().getColor(R.color.brown_light));
        // Toolbar setup
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Recyclerview setup
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Get data instance
        data = Data.getInstance();
        data.setDataLoadedListener(new Data.OnDataLoadedListener() {
            @Override
            public void onDataLoaded() {
                runOnUiThread(() -> {
                    List<Foods> allFoods = data.getFoods();

                    // Attach adapter to recyclerview
                    adapter = new FilteredFoodAdapter(AllFoodsActivity.this, allFoods, data.getDurationMap());
                    binding.recyclerView.setAdapter(adapter);
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