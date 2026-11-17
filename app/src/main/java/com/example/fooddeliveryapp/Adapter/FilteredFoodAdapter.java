package com.example.fooddeliveryapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fooddeliveryapp.Activity.DishDetailActivity;
import com.example.fooddeliveryapp.Domain.Foods;
import com.example.fooddeliveryapp.R;

import java.util.List;
import java.util.Map;

public class FilteredFoodAdapter extends RecyclerView.Adapter<FilteredFoodAdapter.ViewHolder> {

    private List<Foods> foodList;
    private Context context;
    private Map<Integer, String> durationMap;

    // Constructor
    public FilteredFoodAdapter(Context context, List<Foods> foodList, Map<Integer, String> durationMap) {
        this.context = context;
        this.foodList = foodList;
        this.durationMap = durationMap;
    }

    // Create view
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_food, parent, false);
        return new ViewHolder(view);
    }

    // Bind data
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        // Display data
        Foods food = foodList.get(position);
        holder.foodName.setText(food.getName());
        holder.foodDesc.setText(food.getDescription());
        holder.foodPrice.setText("$" + food.getPrice());

        // Get duration
        String durationText = "";
        if (durationMap != null) {
            durationText = durationMap.get(food.getDuration_id());
            if (durationText == null) {
                durationText = "";
            }
        }

        // Display duration
        holder.foodTime.setText(durationText);

        // Load image
        Glide.with(context)
                .load(food.getImage_path())
                .placeholder(R.drawable.placeholder)
                .into(holder.foodImage);

        // Dish details button
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DishDetailActivity.class);
            intent.putExtra("foodId", food.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }

    public void updateList(List<Foods> newList) {
        foodList = newList;
        notifyDataSetChanged();
    }

    // Holds UI components
    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView foodImage;
        TextView foodName, foodDesc, foodPrice, foodTime;

        ViewHolder(View itemView) {
            super(itemView);
            foodImage = itemView.findViewById(R.id.foodImage);
            foodName = itemView.findViewById(R.id.foodName);
            foodDesc = itemView.findViewById(R.id.foodDesc);
            foodPrice = itemView.findViewById(R.id.foodPrice);
            foodTime = itemView.findViewById(R.id.foodDuration);
        }
    }
}