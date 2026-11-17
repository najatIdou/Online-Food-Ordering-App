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
import com.example.fooddeliveryapp.Activity.RestaurantHomeActivity;
import com.example.fooddeliveryapp.Domain.Restaurant;
import com.example.fooddeliveryapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

// Connect data to UI
public class AllRestaurantsAdapter extends RecyclerView.Adapter<AllRestaurantsAdapter.ViewHolder> {

    // Variables
    private Context context;
    private List<Restaurant> restaurantList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onClick(Restaurant restaurant);
    }

    // Constructor
    public AllRestaurantsAdapter(Context context, List<Restaurant> list, OnItemClickListener listener) {
        this.context = context;
        this.restaurantList = list;
        this.listener = listener;
    }

    // Bind data
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.all_restaurant_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Restaurant restaurant = restaurantList.get(position);

        // Display name
        holder.name.setText(restaurant.getName());

        // Default value
        holder.rating.setText("⭐ 0.0");

        // Check restau status
        if (restaurant.getIs_open() == 1) {
            holder.status.setText("OPEN");
            holder.status.setTextColor(context.getResources().getColor(R.color.green));
        } else {
            holder.status.setText("CLOSED");
            holder.status.setTextColor(context.getResources().getColor(android.R.color.holo_red_dark));
        }

        // Load image
        Glide.with(context)
                .load(restaurant.getImage())
                .placeholder(R.drawable.placeholder)
                .into(holder.image);

        // Get restau rating from firebase
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("restaurant_ratings");

        ref.orderByChild("restaurant_id").equalTo(restaurant.getId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        double total = 0;
                        int count = 0;

                        // Calc avg rating
                        for (DataSnapshot s : snapshot.getChildren()) {
                            Double value = s.child("rating").getValue(Double.class);
                            if (value != null) {
                                total += value;
                                count++;
                            }
                        }
                        double avg = count == 0 ? 0 : total / count;
                        holder.rating.setText("⭐ " + String.format("%.1f", avg));
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        holder.rating.setText("⭐ 0.0");
                    }
                });

        // Open restaurant page button
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, RestaurantHomeActivity.class);
            intent.putExtra("restaurantId", restaurant.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return restaurantList.size();
    }

    // Holds UI components
    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        TextView name, rating, status;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.restaurantImage);
            name = itemView.findViewById(R.id.restaurantName);
            rating = itemView.findViewById(R.id.restaurantRating);
            status = itemView.findViewById(R.id.restaurantStatus);
        }
    }
}