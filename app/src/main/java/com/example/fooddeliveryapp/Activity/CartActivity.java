package com.example.fooddeliveryapp.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.fooddeliveryapp.Adapter.CartAdapter;
import com.example.fooddeliveryapp.Data.Cart;
import com.example.fooddeliveryapp.R;
import com.example.fooddeliveryapp.databinding.ActivityCartBinding;

import java.util.ArrayList;

public class CartActivity extends AppCompatActivity {

    // Connect XML UI
    private ActivityCartBinding binding;

    // RecyclerView adapter
    private CartAdapter adapter;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(getResources().getColor(R.color.brown_light));
        // Initialize ViewBinding
        binding = ActivityCartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Toolbar setup
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Recyclerview setup
        binding.cartRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Attach adapter to recyclerview
        adapter = new CartAdapter(this, () -> {
            loadCart();
            updateCartUI();
        });

        binding.cartRecyclerView.setAdapter(adapter);

        //Buttons
        binding.Paymentbtn.setOnClickListener(v ->
                startActivity(new Intent(this, PaymentActivity.class))
        );

        binding.browseFoodBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, AllFoodsActivity.class);
            startActivity(intent);
            finish();
        });
        binding.emptyCartBtn.setOnClickListener(v ->{
            Cart.getInstance().clear();
            binding.cartRecyclerView.setVisibility(View.GONE);
            binding.Paymentbtn.setVisibility(View.GONE);
            binding.emptyCart.setVisibility(View.VISIBLE);
            binding.emptyCartBtn.setVisibility(View.GONE);
            loadCart();
            updateCartUI();
        } );
        // Initial load
        loadCart();
        updateCartUI();
    }

    // Get cart items
    private void loadCart() {
        adapter.setItems(new ArrayList<>(Cart.getInstance().getItems().values()));
        updateTotal();
    }

    // Calc total price
    private void updateTotal() {
        binding.totalPrice.setText("$" + String.format("%.2f", Cart.getInstance().getTotalPrice()));
    }

    private void updateCartUI() {
        // If cart is empty
        if (Cart.getInstance().getItems().isEmpty()) {
            binding.cartRecyclerView.setVisibility(View.GONE);
            binding.Paymentbtn.setVisibility(View.GONE);
            binding.emptyCart.setVisibility(View.VISIBLE);
            binding.emptyCartBtn.setVisibility(View.GONE);
        } else {
            binding.cartRecyclerView.setVisibility(View.VISIBLE);
            binding.Paymentbtn.setVisibility(View.VISIBLE);
            binding.emptyCart.setVisibility(View.GONE);
            binding.emptyCartBtn.setVisibility(View.VISIBLE);
        }
    }

    // Reloads cart
    @Override
    protected void onResume() {
        super.onResume();
        loadCart();
        updateCartUI();
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