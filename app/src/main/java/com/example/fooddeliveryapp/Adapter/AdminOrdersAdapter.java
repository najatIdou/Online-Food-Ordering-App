package com.example.fooddeliveryapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fooddeliveryapp.Data.Data;
import com.example.fooddeliveryapp.Domain.Order;
import com.example.fooddeliveryapp.Domain.Restaurant;
import com.example.fooddeliveryapp.R;

import java.util.List;

public class AdminOrdersAdapter extends RecyclerView.Adapter<AdminOrdersAdapter.ViewHolder> {

    private Context context;
    private List<Order> orders;
    private OnOrderDeleteListener deleteListener;

    public interface OnOrderDeleteListener {
        void onDeleteOrder(Order order);
    }

    // Constructor
    public AdminOrdersAdapter(Context context, List<Order> orders, OnOrderDeleteListener deleteListener) {
        this.context = context;
        this.orders = orders;
        this.deleteListener = deleteListener;
    }

    // Create view
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_admin_order, parent, false);
        return new ViewHolder(view);
    }

    // Bind data
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Order order = orders.get(position);
        Restaurant restaurant = Data.getInstance().getRestaurantById(order.getRestaurant_id());

        if (restaurant != null) {
            // Load image
            Glide.with(context)
                    .load(restaurant.getImage())
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .into(holder.ivRestaurantImage);
        } else {
            holder.ivRestaurantImage.setImageResource(R.drawable.placeholder);
        }

        // Display data
        holder.tvRestaurantName.setText(order.getRestaurant_name());
        holder.tvCustomerName.setText("Customer: " + order.getCustomer_name());
        holder.tvOrderDate.setText("Date: " + order.getCreated_at());
        holder.tvOrderItems.setText("Items: " + order.getItems_summary());
        holder.tvOrderPrice.setText("$" + String.format("%.2f", order.getTotal()));
        holder.tvOrderStatus.setText(order.getStatus());

        // Set status color
        int statusColor;
        switch (order.getStatus().toLowerCase()) {
            case "accepted": statusColor = context.getColor(R.color.green); break;
            case "pending": statusColor = context.getColor(R.color.orange); break;
            case "rejected": statusColor = context.getColor(R.color.red); break;
            case "completed": statusColor = context.getColor(R.color.blue); break;
            default: statusColor = context.getColor(R.color.gray); break;
        }
        holder.tvOrderStatus.setBackgroundColor(statusColor);

        // Delete order button
        holder.btnDeleteOrder.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Confirm")
                    .setMessage("Are you sure to delete the order?")
                    .setPositiveButton("YES", (dialog, which) -> {
                        if (deleteListener != null) {
                            deleteListener.onDeleteOrder(order);
                        }
                    })
                    .setNegativeButton("NO", (dialog, which) -> dialog.dismiss())
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    // Holds UI components
    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivRestaurantImage;
        TextView tvRestaurantName, tvCustomerName, tvOrderDate, tvOrderItems, tvOrderPrice, tvOrderStatus;
        ImageButton btnDeleteOrder;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivRestaurantImage = itemView.findViewById(R.id.ivRestaurantImage);
            tvRestaurantName = itemView.findViewById(R.id.tvRestaurantName);
            tvCustomerName = itemView.findViewById(R.id.tvCustomerName);
            tvOrderDate = itemView.findViewById(R.id.tvOrderDate);
            tvOrderItems = itemView.findViewById(R.id.tvOrderItems);
            tvOrderPrice = itemView.findViewById(R.id.tvOrderPrice);
            tvOrderStatus = itemView.findViewById(R.id.tvOrderStatus);
            btnDeleteOrder = itemView.findViewById(R.id.btnDeleteOrder);
        }
    }
}