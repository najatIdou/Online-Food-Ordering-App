package com.example.fooddeliveryapp.Activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fooddeliveryapp.Domain.Foods;
import com.example.fooddeliveryapp.R;
import com.example.fooddeliveryapp.databinding.ActivityRatingDialogBinding;

import java.util.ArrayList;
import java.util.List;

public class RatingDialogActivity extends android.app.Dialog {

    // Connect XML UI
    private ActivityRatingDialogBinding binding;

    // Send rating data back to calling activity
    public interface RatingListener {
        void onSubmit(float restaurantRating, List<Float> foodRatings);
    }

    private List<Foods> foodList;

    // Recyclerview adapter
    private FoodRatingAdapter adapter;

    public RatingDialogActivity(@NonNull Context context, String restaurantName, List<Foods> foods, RatingListener listener) {
        super(context);

        // Initialize ViewBinding
        binding = ActivityRatingDialogBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        this.foodList = foods;

        // Set restaurant name
        binding.tvRestaurantName.setText(restaurantName);


        // Setup RecyclerView
        binding.recyclerFoodRatings.setLayoutManager(new LinearLayoutManager(context));

        // Attach adapter
        adapter = new FoodRatingAdapter(context, foodList);
        binding.recyclerFoodRatings.setAdapter(adapter);

        // Submit rating button
        binding.btnSubmitRating.setOnClickListener(v -> {
            listener.onSubmit(
                    binding.ratingRestaurant.getRating(),
                    adapter.getRatings()
            );
            dismiss();
        });
    }

    // Adapter
    private static class FoodRatingAdapter extends RecyclerView.Adapter<FoodRatingAdapter.ViewHolder> {

        private List<Foods> foodList;

        // Store rating per item
        private float[] ratings;
        private Context context;

        // Constructor
        FoodRatingAdapter(Context context, List<Foods> foodList) {
            this.context = context;
            this.foodList = foodList;
            this.ratings = new float[foodList.size()];
        }

        // Create item layout
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_food_rating, parent, false);
            return new ViewHolder(view);
        }

        // Bind data to each row
        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

            Foods food = foodList.get(position);

            // Set food name
            holder.tvFoodName.setText(food.getName());

            // Load image
            Glide.with(context)
                    .load(food.getImage_path())
                    .placeholder(R.drawable.placeholder)
                    .into(holder.imgFood);

            // Save rating when user changes it
            holder.ratingBar.setOnRatingBarChangeListener((rb, rating, fromUser) -> {
                ratings[holder.getAdapterPosition()] = rating;
            });
        }

        // Items counter
        @Override
        public int getItemCount() {
            return foodList.size();
        }

        // Return all ratings as list
        List<Float> getRatings() {
            List<Float> list = new ArrayList<>();
            for (float r : ratings) list.add(r);
            return list;
        }

        // Holder of UI components
        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvFoodName;
            RatingBar ratingBar;
            ImageView imgFood;

            ViewHolder(View itemView) {
                super(itemView);
                tvFoodName = itemView.findViewById(R.id.tvFoodName);
                ratingBar = itemView.findViewById(R.id.ratingFoodItem);
                imgFood = itemView.findViewById(R.id.imgFood);
            }
        }
    }
}