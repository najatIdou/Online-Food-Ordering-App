package com.example.fooddeliveryapp.Utils;

import com.example.fooddeliveryapp.Data.Data;
import com.example.fooddeliveryapp.Domain.Foods;
import com.google.firebase.database.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RatingManager {

    // Restaurant Rating
    public static void submitRestaurantRating(int restaurantId, int userId, float rating, int orderId) {

        // Reference to restau ratings table in firebase
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("restaurant_ratings");

        // Read all ratings to generate new Id
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                // Find highest existing Id
                int maxId = 0;
                for (DataSnapshot child : snapshot.getChildren()) {
                    Integer id = child.child("id").getValue(Integer.class);
                    if (id != null && id > maxId){
                        maxId = id;
                    }
                }

                int newId = maxId + 1;
                // Current timestamp
                String createdAt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(new Date());

                // Create new restau rating
                DatabaseReference newRef = ref.child(String.valueOf(newId));
                newRef.child("id").setValue(newId);
                newRef.child("order_id").setValue(orderId);
                newRef.child("restaurant_id").setValue(restaurantId);
                newRef.child("user_id").setValue(userId);
                newRef.child("rating").setValue(rating);
                newRef.child("created_at").setValue(createdAt);
            }

            @Override
            public void onCancelled(DatabaseError error) {}
        });
    }

    // Food Ratings
    public static void submitFoodRatings(List<Foods> foods, int userId, List<Float> ratings, int orderId) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("food_ratings");

        // Read all ratings to generate new Id
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                // Find highest existing Id
                int maxId = 0;
                for (DataSnapshot child : snapshot.getChildren()) {
                    Integer id = child.child("id").getValue(Integer.class);
                    if (id != null && id > maxId){
                        maxId = id;
                    }
                }
                int currentId = maxId;

                for (int i = 0; i < foods.size(); i++) {
                    float rating = ratings.get(i);
                    if (rating <= 0){
                        continue;
                    }
                    currentId++;

                    // Current timestamp
                    String createdAt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(new Date());

                    // Create new food rating
                    DatabaseReference newRef = ref.child(String.valueOf(currentId));
                    newRef.child("id").setValue(currentId);
                    newRef.child("order_id").setValue(orderId);
                    newRef.child("food_id").setValue(foods.get(i).getId());
                    newRef.child("user_id").setValue(userId);
                    newRef.child("rating").setValue(rating);
                    newRef.child("created_at").setValue(createdAt);

                    // Update average for this food
                    updateFoodAverage(foods.get(i).getId());
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {}
        });
    }

    // Update Food Average
    private static void updateFoodAverage(int foodId) {
        DatabaseReference ratingsRef = FirebaseDatabase.getInstance().getReference("food_ratings");

        // Get all ratings for this food
        ratingsRef.orderByChild("food_id").equalTo(foodId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        double total = 0;
                        int count = 0;

                        for (DataSnapshot s : snapshot.getChildren()) {
                            Double r = s.child("rating").getValue(Double.class);
                            if (r != null) {
                                // calc sum ratings
                                total += r;
                                count++;
                            }
                        }

                        // Calc avg
                        double avg = count == 0 ? 0 : total / count;

                        // Save to firebase
                        DatabaseReference foodRef = FirebaseDatabase.getInstance().getReference("foods").child(String.valueOf(foodId));
                        foodRef.child("star").setValue(avg);
                        foodRef.child("rating_count").setValue(count);

                        // Update local data
                        Data data = Data.getInstance();
                        for (Foods food : data.getFoods()) {
                            if (food.getId() == foodId) {
                                food.setStar(avg);
                                break;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {}
                });
    }
}