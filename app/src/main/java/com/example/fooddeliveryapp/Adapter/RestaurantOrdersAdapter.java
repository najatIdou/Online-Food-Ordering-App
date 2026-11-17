package com.example.fooddeliveryapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fooddeliveryapp.Domain.Order;
import com.example.fooddeliveryapp.R;

import java.util.List;

public class RestaurantOrdersAdapter extends RecyclerView.Adapter<RestaurantOrdersAdapter.ViewHolder> {

    private List<Order> orders;
    private Context context;
    private boolean isPending;
    private OnOrderActionListener listener;

    public interface OnOrderActionListener {
        void onAccept(Order order);
        void onReject(Order order);
        void onComplete(Order order);
    }

    // Constructor
    public RestaurantOrdersAdapter(Context context, List<Order> orders, boolean isPending, OnOrderActionListener listener) {
        this.context = context;
        this.orders = orders;
        this.isPending = isPending;
        this.listener = listener;
    }

    // Create view
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order_restaurant, parent, false);
        return new ViewHolder(view);
    }

    // Bind data
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Order order = orders.get(position);

        // Display data
        holder.orderNumber.setText("Order #" + (order.getOrder_number() != null ? order.getOrder_number() : String.valueOf(order.getId())));
        holder.orderTotal.setText("$" + String.format("%.2f", order.getTotal()));
        holder.orderTime.setText(order.getCreated_at() != null ? order.getCreated_at() : "");
        holder.customerName.setText(order.getCustomer_name() != null ? order.getCustomer_name() : "Unknown");
        holder.customerPhone.setText(order.getCustomer_phone() != null ? order.getCustomer_phone() : "N/A");
        holder.customerAddress.setText(order.getDelivery_address() != null ? order.getDelivery_address() : "N/A");
        holder.itemsSummary.setText(order.getItems_summary() != null ? order.getItems_summary() : "");

        if (isPending) {
            holder.btnAccept.setVisibility(View.VISIBLE);
            holder.btnReject.setVisibility(View.VISIBLE);
            holder.btnComplete.setVisibility(View.GONE);
            holder.statusContainer.setVisibility(View.GONE);

            holder.btnAccept.setOnClickListener(v -> listener.onAccept(order));
            holder.btnReject.setOnClickListener(v -> listener.onReject(order));
        } else {
            holder.btnAccept.setVisibility(View.GONE);
            holder.btnReject.setVisibility(View.GONE);

            if (order.getStatus().equals(Order.STATUS_ACCEPTED)) {
                holder.btnComplete.setVisibility(View.VISIBLE);
                holder.statusContainer.setVisibility(View.GONE);

                holder.btnComplete.setOnClickListener(v -> {
                    if (listener != null) listener.onComplete(order);
                });

            } else {
                holder.btnComplete.setVisibility(View.GONE);
                holder.statusContainer.setVisibility(View.VISIBLE);

                if (order.getStatus().equals(Order.STATUS_COMPLETED)) {
                    holder.statusIcon.setImageResource(R.drawable.check_circle);
                    holder.statusText.setText("Order Completed");
                    holder.statusText.setTextColor(context.getResources().getColor(R.color.blue));
                    holder.statusContainer.setBackgroundResource(R.drawable.status_background_blue);
                } else if(order.getStatus().equals(Order.STATUS_REJECTED)){
                    holder.statusIcon.setImageResource(R.drawable.ic_close);
                    holder.statusText.setText("Order Rejected");
                    holder.statusText.setTextColor(context.getResources().getColor(R.color.red));
                    holder.statusContainer.setBackgroundResource(R.drawable.status_background_red);
                } else if(order.getStatus().equals(Order.STATUS_ACCEPTED)){
                    holder.statusIcon.setImageResource(R.drawable.ic_check);
                    holder.statusText.setText("Order Accepted");
                    holder.statusText.setTextColor(context.getResources().getColor(R.color.green));
                    holder.statusContainer.setBackgroundResource(R.drawable.status_background_green);
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    // Holds UI components
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView orderNumber, orderTotal, orderTime, customerName, customerPhone, customerAddress, itemsSummary, statusText;
        Button btnAccept, btnReject, btnComplete;
        LinearLayout statusContainer;
        ImageView statusIcon;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            orderNumber = itemView.findViewById(R.id.tvOrderId);
            orderTotal = itemView.findViewById(R.id.orderTotal);
            orderTime = itemView.findViewById(R.id.tvTime);
            customerName = itemView.findViewById(R.id.orderCustomer);
            customerPhone = itemView.findViewById(R.id.tvPhone);
            customerAddress = itemView.findViewById(R.id.tvAddress);
            itemsSummary = itemView.findViewById(R.id.tvItems);
            btnAccept = itemView.findViewById(R.id.btnAccept);
            btnComplete = itemView.findViewById(R.id.btnComplete);
            btnReject = itemView.findViewById(R.id.btnReject);
            statusContainer = itemView.findViewById(R.id.statusContainer);
            statusIcon = itemView.findViewById(R.id.statusIcon);
            statusText = itemView.findViewById(R.id.statusText);
        }
    }
}