package com.example.fooddeliveryapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fooddeliveryapp.Data.Cart;
import com.example.fooddeliveryapp.R;

import java.util.ArrayList;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    private List<Cart.CartItem> items = new ArrayList<>();
    private Context context;
    private OnQuantityChangeListener listener;

    // Constructor
    public CartAdapter(Context context, OnQuantityChangeListener listener) {
        this.context = context;
        this.listener = listener;
    }

    // Updte list and refresh UI
    public void setItems(List<Cart.CartItem> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    // Create view
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        return new ViewHolder(view);
    }

    // Bind data
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Cart.CartItem item = items.get(position);
        var food = item.getFood();

        // Display data
        holder.name.setText(food.getName());
        holder.price.setText("$" + food.getPrice());
        holder.quantity.setText(String.valueOf(item.getQuantity()));
        holder.total.setText("$" + (food.getPrice() * item.getQuantity()));

        // Load image
        Glide.with(context)
                .load(food.getImage_path())
                .placeholder(R.drawable.placeholder)
                .into(holder.image);

        // Minus button
        holder.minus.setOnClickListener(v -> {
            int newQty = item.getQuantity() - 1;
            if (newQty <= 0) {
                Cart.getInstance().removeItem(food.getId());
            } else {
                Cart.getInstance().updateQuantity(food.getId(), newQty);
            }
            if (listener != null) {
                listener.onQuantityChanged();
            }
        });

        // Plus button
        holder.plus.setOnClickListener(v -> {
            Cart.getInstance().updateQuantity(food.getId(), item.getQuantity() + 1);

            if (listener != null) {
                listener.onQuantityChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    // Holds UI components
    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        Button minus, plus;
        TextView name, price, quantity, total;

        ViewHolder(View v) {
            super(v);
            image = v.findViewById(R.id.foodImage);
            name = v.findViewById(R.id.foodName);
            price = v.findViewById(R.id.foodPrice);
            quantity = v.findViewById(R.id.quantityValue);
            total = v.findViewById(R.id.orderTotal);
            minus = v.findViewById(R.id.quantityMinus);
            plus = v.findViewById(R.id.quantityPlus);
        }
    }

    public interface OnQuantityChangeListener {
        void onQuantityChanged();
    }
}