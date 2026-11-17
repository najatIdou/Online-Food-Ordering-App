package com.example.fooddeliveryapp.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fooddeliveryapp.Activity.RatingDialogActivity;
import com.example.fooddeliveryapp.Data.Data;
import com.example.fooddeliveryapp.Domain.Foods;
import com.example.fooddeliveryapp.Domain.Order;
import com.example.fooddeliveryapp.Domain.Restaurant;
import com.example.fooddeliveryapp.R;
import com.example.fooddeliveryapp.Utils.RatingManager;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {

    private Context context;
    private List<Order> orders;

    // Constructor
    public OrderAdapter(Context context, List<Order> orders) {
        this.context = context;
        this.orders = orders;
    }

    // Create view
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_order, parent, false);
        return new ViewHolder(view);
    }

    // Bind data
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Order order = orders.get(position);

        holder.tvRestaurantName.setText(order.getRestaurant_name());
        holder.tvItemsSummary.setText(order.getItems_summary());
        holder.tvOrderTotal.setText("$" + String.format("%.2f", order.getTotal()));
        holder.tvOrderNumber.setText("Order #" + order.getOrder_number());
        holder.tvOrderDate.setText(formatDate(order.getCreated_at()));
        holder.tvOrderStatus.setText(formatStatus(order.getStatus()));

        // Cancel button for pending orders
        if (Order.STATUS_PENDING.equals(order.getStatus())) {
            holder.btnCancelOrder.setVisibility(View.VISIBLE);
        } else {
            holder.btnCancelOrder.setVisibility(View.GONE);
        }

        setStatusColor(holder.tvOrderStatus, order.getStatus());

        // Load image
        Restaurant restaurant = Data.getInstance().getRestaurantById(order.getRestaurant_id());
        if (restaurant != null && restaurant.getImage() != null) {
            Glide.with(context)
                    .load(restaurant.getImage())
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .into(holder.ivRestaurantImage);
        } else {
            holder.ivRestaurantImage.setImageResource(R.drawable.placeholder);
        }

        // Rate button for completed and unrated orders
        if (Order.STATUS_COMPLETED.equalsIgnoreCase(order.getStatus())) {
            if (!Data.getInstance().isOrderRated(order.getId())) {
                holder.btnRateOrder.setVisibility(View.VISIBLE);
                holder.tvRated.setVisibility(View.GONE);
            } else {
                holder.btnRateOrder.setVisibility(View.GONE);
                holder.tvRated.setVisibility(View.VISIBLE);
            }
        } else {
            holder.btnRateOrder.setVisibility(View.GONE);
            holder.tvRated.setVisibility(View.GONE);
        }

        // Cancel order button
        holder.btnCancelOrder.setOnClickListener(v -> new AlertDialog.Builder(context)
                .setTitle("Cancel Order")
                .setMessage("Are you sure you want to cancel this order? It cannot be undone.")
                .setPositiveButton("Yes", (dialog, which) -> {
                    DatabaseReference orderRef = FirebaseDatabase.getInstance()
                            .getReference("orders")
                            .child(String.valueOf(order.getId()));
                    orderRef.child("status").setValue(Order.STATUS_CANCELED)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(context, "Order canceled successfully", Toast.LENGTH_SHORT).show();
                                // Refresh adapter data
                                updateOrders(Data.getInstance().getOrdersForUser(order.getUser_id()));
                                notifyDataSetChanged();
                            })
                            .addOnFailureListener(e -> Toast.makeText(context, "Failed to cancel order", Toast.LENGTH_SHORT).show());
                })
                .setNegativeButton("No", null)
                .show());

        // Rating button
        holder.btnRateOrder.setOnClickListener(v -> {
            List<Foods> foodItems = Data.getInstance().getFoodsForOrder(order.getId());
            // Open rating dialog
            RatingDialogActivity dialog = new RatingDialogActivity(context, order.getRestaurant_name(), foodItems, (restaurantRating, foodRatings) -> {
                        int userId = context.getSharedPreferences("UserData", Context.MODE_PRIVATE).getInt("userId", -1);
                        // Submit ratings
                        if (restaurantRating > 0) {
                            RatingManager.submitRestaurantRating(order.getRestaurant_id(), userId, restaurantRating, order.getId());
                        }
                        RatingManager.submitFoodRatings(foodItems, userId, foodRatings, order.getId());
                        Data.getInstance().addRatedOrder(order.getId());
                        holder.btnRateOrder.setVisibility(View.GONE);
                        holder.tvRated.setVisibility(View.VISIBLE);
                        Toast.makeText(context, "Ratings submitted!", Toast.LENGTH_SHORT).show();
                    }
            );
            dialog.show();
        });
    }

    public void updateOrders(List<Order> newOrders) {
        this.orders = newOrders;
    }

    private String formatStatus(String status) {
        if (status == null) return "";
        switch (status.toLowerCase(Locale.US)) {
            case Order.STATUS_PENDING:
                return "Pending";
            case Order.STATUS_ACCEPTED:
                return "Accepted";
            case Order.STATUS_REJECTED:
                return "Rejected";
            case Order.STATUS_COMPLETED:
                return "Completed";
            case Order.STATUS_CANCELED:
                return "Canceled";
            default:
                return status;
        }
    }

    private String formatDate(String dateStr) {
        try {
            SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
            Date date = input.parse(dateStr);
            SimpleDateFormat output = new SimpleDateFormat("MMM dd, yyyy", Locale.US);
            return output.format(date);
        } catch (Exception e) {
            return dateStr;
        }
    }

    @SuppressLint("ResourceType")
    private void setStatusColor(TextView tv, String status) {
        int color;
        switch (status) {
            case Order.STATUS_PENDING:
                color = context.getColor(android.R.color.holo_orange_dark);
                break;
            case Order.STATUS_ACCEPTED:
                color = context.getColor(android.R.color.holo_green_dark);
                break;
            case Order.STATUS_REJECTED:
                color = context.getColor(android.R.color.holo_red_dark);
                break;
            case Order.STATUS_COMPLETED:
                color = context.getColor(android.R.color.holo_blue_dark);
                break;
            case Order.STATUS_CANCELED:
                color = context.getColor(android.R.color.darker_gray);
                break;
            default:
                color = context.getColor(android.R.color.darker_gray);
        }
        tv.setTextColor(Color.WHITE);
        tv.setBackgroundColor(color);
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    // Holds UI components
    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivRestaurantImage;
        TextView tvRestaurantName, tvOrderStatus, tvOrderDate, tvItemsSummary, tvOrderTotal, tvOrderNumber, tvRated;
        Button btnRateOrder, btnCancelOrder;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivRestaurantImage = itemView.findViewById(R.id.ivRestaurantImage);
            tvRestaurantName = itemView.findViewById(R.id.tvRestaurantName);
            tvOrderStatus = itemView.findViewById(R.id.tvOrderStatus);
            tvOrderDate = itemView.findViewById(R.id.tvOrderDate);
            tvItemsSummary = itemView.findViewById(R.id.tvItemsSummary);
            btnCancelOrder = itemView.findViewById(R.id.btnCancelOrder);
            tvOrderTotal = itemView.findViewById(R.id.tvOrderTotal);
            tvOrderNumber = itemView.findViewById(R.id.tvOrderNumber);
            btnRateOrder = itemView.findViewById(R.id.btnRateOrder);
            tvRated = itemView.findViewById(R.id.tvRated);
        }
    }
}