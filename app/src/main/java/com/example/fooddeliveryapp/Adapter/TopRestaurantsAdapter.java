package com.example.fooddeliveryapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fooddeliveryapp.R;

import java.util.List;

public class TopRestaurantsAdapter extends RecyclerView.Adapter<TopRestaurantsAdapter.ViewHolder> {

    private Context context;
    private List<RestaurantCount> list;

    public static class RestaurantCount {
        public String name;
        public int count;
        public RestaurantCount(String name, int count) {
            this.name = name;
            this.count = count;
        }
    }

    // Constructor
    public TopRestaurantsAdapter(Context context, List<RestaurantCount> list) {
        this.context = context;
        this.list = list;
    }

    // Create view
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_top_restaurant, parent, false);
        return new ViewHolder(view);
    }

    // Bind data
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RestaurantCount item = list.get(position);
        holder.tvRestaurantName.setText(item.name);
        holder.tvOrderCount.setText(item.count + " orders");
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    // Holds UI components
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvRestaurantName, tvOrderCount;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRestaurantName = itemView.findViewById(R.id.tvRestaurantName);
            tvOrderCount = itemView.findViewById(R.id.tvOrderCount);
        }
    }
}