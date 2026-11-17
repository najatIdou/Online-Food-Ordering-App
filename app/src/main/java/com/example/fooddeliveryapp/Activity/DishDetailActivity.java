package com.example.fooddeliveryapp.Activity;

import static android.view.View.VISIBLE;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.fooddeliveryapp.Data.Cart;
import com.example.fooddeliveryapp.Data.Data;
import com.example.fooddeliveryapp.Domain.Foods;
import com.example.fooddeliveryapp.R;
import com.example.fooddeliveryapp.databinding.ActivityDishDetailBinding;

import java.util.Locale;

public class DishDetailActivity extends AppCompatActivity {

    // Connect XML UI
    private ActivityDishDetailBinding binding;
    private int foodId;
    private Foods food;

    // Initial quantity
    private int quantity = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize ViewBinding
        binding = ActivityDishDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getWindow().setStatusBarColor(getResources().getColor(R.color.black));
        // Get foodId
        foodId = getIntent().getIntExtra("foodId", -1);

        // Initialize
        initViews();
        loadFood();
        setupQuantityControls();
        setupAddToCart();

        // Back button
        binding.backButton.setOnClickListener(v -> {
            finish();
        });
        binding.cartContainer.setOnClickListener(v -> {
            startActivity(new Intent(this, CartActivity.class));
        });
        updateCartBadge();
        syncCartUI();
    }

    // Cart badge
    private void updateCartBadge() {

        // Per food
        Cart.CartItem item = Cart.getInstance().getItems().get(food.getId());
        int count = item != null ? item.getQuantity() : 0;
        if (count > 0) {
            binding.cartBadge.setText(String.valueOf(count));
            binding.cartBadge.setVisibility(View.VISIBLE);
        } else {
            binding.cartBadge.setVisibility(View.GONE);
        }
    }
    private void setupQuantityControls() {
    }

    // Plus Minus click
    private void initViews() {

        binding.quantityMinus.setOnClickListener(v -> updateQuantity(-1));
        binding.quantityPlus.setOnClickListener(v -> updateQuantity(1));
    }
    private void loadFood() {
        // Load food
        Data data = Data.getInstance();
        for (Foods f : data.getFoods()) {
            if (f.getId() == foodId) {
                food = f;
                break;
            }
        }
        // If not found
        if (food == null) {
            Toast.makeText(this, "Food not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Display data
        binding.foodPrice.setText("$" + food.getPrice());

        // Load image
        Glide.with(this).
                load(food.getImage_path()).
                placeholder(R.drawable.placeholder).
                into(binding.foodImage);
        binding.foodName.setText(food.getName());
        // Find category name
        String categoryName = "";
        for (var cat : data.getCategories()) {
            if (cat.getId() == food.getCategory_id()) {
                categoryName = cat.getName();
                break;
            }
        }
        binding.addToCartBtn.setText("Add to Cart - $" + food.getPrice());
        binding.foodCategory.setText(categoryName);
        binding.foodDescription.setText(food.getDescription());
        // Set food rating
        float rating = food.getStar() != null ? food.getStar().floatValue() : 0f;
        binding.foodRatingBar.setRating(rating);
        binding.foodRatingText.setText(String.format(Locale.getDefault(), "%.1f", rating));
    }

    // Update cart badge
    private void syncCartUI() {
        if (food == null) return;
        Cart.CartItem item = Cart.getInstance().getItems().get(food.getId());
        if (item != null) {
            // Food already in the cart
            quantity = item.getQuantity();
            binding.quantityValue.setText(String.valueOf(quantity));
            findViewById(R.id.cartContainer).setVisibility(View.VISIBLE);
        } else {
            // Food not in cart
            quantity = 1;
            binding.quantityValue.setText(String.valueOf(quantity));
            findViewById(R.id.cartContainer).setVisibility(View.GONE);
        }
    }

    // Quantity control
    private void updateQuantity(int delta) {
        quantity += delta;
        if (quantity < 1) quantity = 1;
        binding.quantityValue.setText(String.valueOf(quantity));
    }

    private void setupAddToCart() {
        binding.addToCartBtn.setOnClickListener(v -> {
            findViewById(R.id.cartContainer).setVisibility(VISIBLE);
            if (food == null) {
                Toast.makeText(this, "Food is NULL", Toast.LENGTH_SHORT).show();
                return;
            }

            // Add to cart
            Cart.getInstance().setItemQuantity(food, quantity);
            updateCartBadge();
            Toast.makeText(this, "Added to cart", Toast.LENGTH_SHORT).show();
        });
    }
    // Reload cart
    @Override
    protected void onResume() {
        super.onResume();
        updateCartBadge();
    }
}