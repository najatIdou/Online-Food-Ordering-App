package com.example.fooddeliveryapp.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fooddeliveryapp.Domain.Foods;
import com.example.fooddeliveryapp.R;

import java.util.List;
import java.util.Map;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.ViewHolder> {

    private final Context context;
    private final List<Foods> foods;
    private final OnItemClickListener listener;
    private Map<Integer, String> durationMap;

    public interface OnItemClickListener {
        void onItemClick(Foods food);
    }

    // Constructor
    public FoodAdapter(Context context, List<Foods> foods, OnItemClickListener listener) {
        this.context = context;
        this.foods = foods;
        this.listener = listener;
    }

    public void setDurationMap(Map<Integer, String> durationMap) {
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
        Foods food = foods.get(position);

        // Load image
        String imagePath = food.getImage_path();
        if (imagePath != null && imagePath.startsWith("data:image")) {
            try {
                String base64Image = imagePath.substring(imagePath.indexOf(",") + 1);
                byte[] decodedBytes = Base64.decode(base64Image, Base64.DEFAULT);

                Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                holder.image.setImageBitmap(bitmap);

            } catch (Exception e) {
                holder.image.setImageResource(R.drawable.placeholder);
            }
        } else {
            Glide.with(context)
                    .load(imagePath)
                    .placeholder(R.drawable.placeholder)
                    .into(holder.image);
        }

        // Display data
        holder.name.setText(food.getName());
        holder.price.setText("$" + food.getPrice());
        holder.description.setText(food.getDescription());

        // Get duration
        if (durationMap != null && food.getDuration_id() != -1) {
            String duration = durationMap.get(food.getDuration_id());
            holder.duration.setText(duration);
        } else {
            holder.duration.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> listener.onItemClick(food));
    }

    @Override
    public int getItemCount() {
        return foods.size();
    }

    // Holds UI components
    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView name, price, description, duration;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.foodImage);
            name = itemView.findViewById(R.id.foodName);
            price = itemView.findViewById(R.id.foodPrice);
            description = itemView.findViewById(R.id.foodDesc);
            duration = itemView.findViewById(R.id.foodDuration);
        }
    }
}