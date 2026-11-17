package com.example.fooddeliveryapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fooddeliveryapp.Domain.Restaurant;
import com.example.fooddeliveryapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class RestaurantListAdapter extends RecyclerView.Adapter<RestaurantListAdapter.ViewHolder> {
    private List<Restaurant> restaurants;
    private Context context;
    private OnRestaurantClickListener listener;

    public interface OnRestaurantClickListener {
        void onRestaurantClick(int restaurantId);
    }

    // Constructor
    public RestaurantListAdapter(Context context, List<Restaurant> restaurants, OnRestaurantClickListener listener) {
        this.context = context;
        this.restaurants = restaurants;
        this.listener = listener;
    }

    // Create view
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_restaurant, parent, false);
        return new ViewHolder(view);
    }

    // Bind data
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Restaurant r = restaurants.get(position);

        // Display name
        holder.name.setText(r.getName());
        // Show open, close status
        if (r.getIs_open() == 1) {
            holder.status.setText("OPEN");
            holder.status.setTextColor(context.getResources().getColor(R.color.green));
        } else {
            holder.status.setText("CLOSED");
            holder.status.setTextColor(context.getResources().getColor(android.R.color.holo_red_dark));
        }

        // Load image
        Glide.with(context)
                .load(r.getImage())
                .placeholder(R.drawable.placeholder)
                .into(holder.image);

        // Default value
        holder.rating.setText("0.0");

        // Get restaurant ratings
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("restaurant_ratings");
        ref.orderByChild("restaurant_id").equalTo(r.getId())
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

                        // Display rating
                        holder.rating.setText(String.format("⭐ %.1f",avg));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        holder.rating.setText("0.0");
                    }
                });

        holder.itemView.setOnClickListener(v -> listener.onRestaurantClick(r.getId()));
    }

    @Override
    public int getItemCount() {
        return restaurants.size();
    }

    // Holds UI components
    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView name, rating, status;

        ViewHolder(View v) {
            super(v);
            image = v.findViewById(R.id.restaurantImage);
            name = v.findViewById(R.id.restaurantName);
            rating = v.findViewById(R.id.restaurantRating);
            status = v.findViewById(R.id.restaurantStatus);
        }
    }
}