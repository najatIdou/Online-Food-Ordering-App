package com.example.fooddeliveryapp.Data;

import android.util.Log;
import androidx.annotation.NonNull;
import com.example.fooddeliveryapp.Domain.Category;
import com.example.fooddeliveryapp.Domain.Foods;
import com.example.fooddeliveryapp.Domain.Location;
import com.example.fooddeliveryapp.Domain.Restaurant;
import com.example.fooddeliveryapp.Domain.Time;
import com.example.fooddeliveryapp.Domain.Order;
import com.example.fooddeliveryapp.Domain.Address;
import com.example.fooddeliveryapp.Domain.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Data {
    private static Data instance;

    // Stores categories, foods, locations, orders, addresses, user, restaurants from firebase
    private List<Category> categoryList = new ArrayList<>();
    private List<Foods> foodsList = new ArrayList<>();
    private List<Location> locationList = new ArrayList<>();
    private List<Restaurant> restaurantList = new ArrayList<>();
    private List<Order> orderList = new ArrayList<>();
    private List<Address> addressList = new ArrayList<>();
    private List<User> userList = new ArrayList<>();
    // Map durations, already rated orders
    private Map<Integer, String> durationMap = new HashMap<>();
    private Map<Integer, Boolean> ratedOrdersMap = new HashMap<>();
    private OnDataLoadedListener dataLoadedListener;

    private int loadedCount = 0;

    // categories, foods, restaurants, durations, locations, orders, addresses, users
    private final int totalDataTypes = 8;

    // Constructor
    private Data() {
        loadAllData();
    }

    public static synchronized Data getInstance() {
        if (instance == null) {
            instance = new Data();
        }
        return instance;
    }

    // Loads all firebase data
    private void loadAllData() {
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();

        // Load categories
        rootRef.child("categories").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
               // Remove old data
                categoryList.clear();
                for (DataSnapshot catSnap : snapshot.getChildren()) {
                    Category cat = catSnap.getValue(Category.class);
                    if (cat != null){
                        categoryList.add(cat);
                    }
                }
                GetCustomerName(orderList);
                // Tracks loading progress
                checkAllDataLoaded();
                for (Foods food : foodsList) {
                    updateFoodAverageRating(food.getId());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Data", "Failed to load categories", error.toException());
                checkAllDataLoaded();
            }
        });

        // Load foods
        rootRef.child("foods").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Remove old data
                foodsList.clear();
                for (DataSnapshot foodSnap : snapshot.getChildren()) {
                    Foods food = foodSnap.getValue(Foods.class);
                    if (food != null) foodsList.add(food);
                }
                // Tracks loading progress
                checkAllDataLoaded();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Data", "Failed to load foods", error.toException());
                checkAllDataLoaded();
            }
        });

        // Load restaurants
        rootRef.child("restaurants").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Remove old data
                restaurantList.clear();
                for (DataSnapshot resSnap : snapshot.getChildren()) {
                    Restaurant res = resSnap.getValue(Restaurant.class);
                    if (res != null) restaurantList.add(res);
                }
                // Tracks loading progress
                checkAllDataLoaded();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Data", "Failed to load restaurants", error.toException());
                checkAllDataLoaded();
            }
        });

        // Load durations
        rootRef.child("durations").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Remove old data
                durationMap.clear();
                for (DataSnapshot durSnap : snapshot.getChildren()) {
                    Time time = durSnap.getValue(Time.class);
                    if (time != null) {
                        durationMap.put(time.getId(), time.getValue());
                    }
                }
                // Tracks loading progress
                checkAllDataLoaded();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Data", "Failed to load durations", error.toException());
                checkAllDataLoaded();
            }
        });

        // Load locations
        rootRef.child("locations").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Remove old data
                locationList.clear();
                for (DataSnapshot locSnap : snapshot.getChildren()) {
                    Location loc = locSnap.getValue(Location.class);
                    if (loc != null) locationList.add(loc);
                }
                // Tracks loading progress
                checkAllDataLoaded();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Data", "Failed to load locations", error.toException());
                checkAllDataLoaded();
            }
        });

        // Load orders
        rootRef.child("orders").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Remove old data
                orderList.clear();
                for (DataSnapshot orderSnap : snapshot.getChildren()) {
                    Order order = orderSnap.getValue(Order.class);
                    if (order != null) {
                        orderList.add(order);
                    }
                }
                // Tracks loading progress
                checkAllDataLoaded();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Data", "Failed to load orders", error.toException());
                checkAllDataLoaded();
            }
        });

        // Load addresses
        rootRef.child("addresses").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Remove old data
                addressList.clear();
                for (DataSnapshot addrSnap : snapshot.getChildren()) {
                    Address addr = addrSnap.getValue(Address.class);
                    if (addr != null) addressList.add(addr);
                }
                // Tracks loading progress
                checkAllDataLoaded();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Data", "Failed to load addresses", error.toException());
                checkAllDataLoaded();
            }
        });

        // Load users
        rootRef.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Remove old data
                userList.clear();
                for (DataSnapshot userSnap : snapshot.getChildren()) {
                    User user = userSnap.getValue(User.class);
                    if (user != null) userList.add(user);
                }
                // Tracks loading progress
                checkAllDataLoaded();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Data", "Failed to load users", error.toException());
                checkAllDataLoaded();
            }
        });
    }

    // Check loaded data
    private synchronized void checkAllDataLoaded() {
        // Counts how many datasets finished loading
        loadedCount++;
        if (loadedCount == totalDataTypes && dataLoadedListener != null) {
           // If all 8 datasets loaded notify UI
            dataLoadedListener.onDataLoaded();
        }
    }

    public void setDataLoadedListener(OnDataLoadedListener listener) {
        this.dataLoadedListener = listener;
        if (loadedCount == totalDataTypes) {
            listener.onDataLoaded();
        }
    }

    // Notify UI
    public interface OnDataLoadedListener {
        void onDataLoaded();
    }

    // Getters
    public List<Category> getCategories() {
        return new ArrayList<>(categoryList);
    }
    public List<Foods> getFoods() {
        return new ArrayList<>(foodsList);
    }
    public List<Location> getLocations() {
        return new ArrayList<>(locationList);
    }
    public Map<Integer, String> getDurationMap() {
        return durationMap;
    }
    public List<Restaurant> getRestaurants() {
        return new ArrayList<>(restaurantList);
    }
    public Restaurant getRestaurantById(int id) {
        for (Restaurant r : restaurantList) {
            if (r.getId() == id) {
                return r;
            }
        }
        return null;
    }
    public List<Order> getOrders() {
        return new ArrayList<>(orderList);
    }

    // Filter orders by userId
    public List<Order> getOrdersForUser(int userId) {
        List<Order> userOrders = new ArrayList<>();
        for (Order order : orderList) {
            if (order.getUser_id() == userId) {
                userOrders.add(order);
            }
        }
        // Sort latest first
        userOrders.sort((o1, o2) -> o2.getCreated_at().compareTo(o1.getCreated_at()));
        return userOrders;
    }
    public List<Address> getAddresses() {
        return new ArrayList<>(addressList);
    }
    public List<User> getUsers() {
        return new ArrayList<>(userList);
    }
    public User getUserById(int userId) {
        for (User u : userList) {
            if (u.getId() == userId) return u;
        }
        return null;
    }
    public List<Foods> getFoodsForOrder(int orderId) {
        List<Foods> result = new ArrayList<>();
        Order order = null;
        for (Order o : orderList) {
            if (o.getId() == orderId) {
                order = o;
                break;
            }
        }
        if (order == null || order.getItems_summary() == null) {
            Log.e("Data", "Order not found or no items_summary for order " + orderId);
            return result;
        }

        String summary = order.getItems_summary();
        Log.d("Data", "Parsing items_summary: " + summary);

        // Split by commas
        String[] items = summary.split(",");
        for (String item : items) {
            item = item.trim();
            if (item.isEmpty()) continue;

            // Match Itemname x2 or Itemname
            Pattern pattern = Pattern.compile("(.+?)(?:\\s*x\\s*(\\d+))?$");
            Matcher matcher = pattern.matcher(item);
            if (matcher.matches()) {
                String itemName = matcher.group(1).trim();
                int quantity = 1;
                if (matcher.group(2) != null) {
                    quantity = Integer.parseInt(matcher.group(2));
                }

                // Find matching food in foodsList
                Foods matchedFood = null;
                for (Foods food : foodsList) {
                    if (food.getName().equalsIgnoreCase(itemName)) {
                        matchedFood = food;
                        break;
                    }
                }
                // Case insensitive
                if (matchedFood == null) {
                    for (Foods food : foodsList) {
                        if (food.getName().toLowerCase().contains(itemName.toLowerCase()) ||
                                itemName.toLowerCase().contains(food.getName().toLowerCase())) {
                            matchedFood = food;
                            break;
                        }
                    }
                }

                // Add items to result list
                if (matchedFood != null) {
                    for (int i = 0; i < quantity; i++) {
                        result.add(matchedFood);
                    }
                    Log.d("Data", "Added " + quantity + "x " + matchedFood.getName());
                } else {
                    Log.w("Data", "No food match for: " + itemName);
                }
            } else {
                Log.w("Data", "Item did not match pattern: " + item);
            }
        }
        Log.d("Data", "Total food items for order " + orderId + ": " + result.size());
        return result;
    }

    // Loads ratings for a user
    public void loadUserRatings(int userId, OnUserRatingsLoadedListener listener) {
        DatabaseReference restRef = FirebaseDatabase.getInstance().getReference("restaurant_ratings");
        restRef.orderByChild("user_id").equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Remove old data
                ratedOrdersMap.clear();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    Integer orderId = snap.child("order_id").getValue(Integer.class);
                    if (orderId != null) {
                        ratedOrdersMap.put(orderId, true);
                    }
                }
                if (listener != null){
                    listener.onLoaded();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Data", "Failed to load user ratings", error.toException());
            }
        });
    }

    // If order rating exist
    public boolean isOrderRated(int orderId) {
        return ratedOrdersMap.containsKey(orderId);
    }

    public void addRatedOrder(int orderId) {
        ratedOrdersMap.put(orderId, true);
    }
    private void updateFoodAverageRating(int foodId) {

        // Reference to food rating table in firebase
        DatabaseReference foodRatingsRef = FirebaseDatabase.getInstance().getReference("food_ratings");
        foodRatingsRef.orderByChild("food_id")
                .equalTo(foodId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        float sum = 0;
                        int count = 0;
                        for (DataSnapshot ratingSnap : snapshot.getChildren()) {
                            Double rating = ratingSnap.child("rating").getValue(Double.class);
                            // Calc avg
                            if (rating != null) {
                                sum += rating;
                                count++;
                            }
                        }
                        float avg = (count == 0) ? 0 : sum / count;

                        DatabaseReference foodRef = FirebaseDatabase.getInstance().getReference("foods").child(String.valueOf(foodId));

                        // Create rating object
                        Map<String, Object> updates = new HashMap<>();
                        updates.put("star", avg);
                        updates.put("rating_count", count);

                        // Update firebase
                        foodRef.updateChildren(updates)
                                .addOnSuccessListener(aVoid -> {
                                    // Update local list
                                    for (Foods food : foodsList) {
                                        if (food.getId() == foodId) {
                                            food.setStar((double) avg);
                                            break;
                                        }
                                    }
                                    // Update best foods
                                    updateBestFoods();
                                })
                                .addOnFailureListener(e -> Log.e("Data", "Failed to update rating", e));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("Data", "Rating fetch failed", error.toException());
                    }
                });
    }

    public void updateBestFoods() {
        List<Foods> sorted = new ArrayList<>(foodsList);
        // Sort food by rating
        sorted.sort((f1, f2) -> Double.compare(f2.getStar(), f1.getStar()));
        int topN = 10;

        // Reset all best_food flags to 0
        for (Foods food : foodsList) {
            if (food.getBest_food() != 0) {
                food.setBest_food(0);
                DatabaseReference foodRef = FirebaseDatabase.getInstance().getReference("foods").child(String.valueOf(food.getId()));
                foodRef.child("best_food").setValue(0);
            }
        }

        // Set to 10 best food
        for (int i = 0; i < Math.min(topN, sorted.size()); i++) {
            Foods food = sorted.get(i);
            if (food.getStar() > 0) {
                food.setBest_food(1);
                DatabaseReference foodRef = FirebaseDatabase.getInstance().getReference("foods").child(String.valueOf(food.getId()));
                foodRef.child("best_food").setValue(1);
            }
        }
    }

    public void reloadOrders(Runnable onDone) {
        // Reference to orders table in firebase
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("orders");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Clear old data
                orderList.clear();
                for (DataSnapshot orderSnap : snapshot.getChildren()) {
                    Order order = orderSnap.getValue(Order.class);
                    if (order != null) {
                        orderList.add(order);
                        GetCustomerName(orderList);
                    }
                }
                Log.d("Data", "Orders reloaded: " + orderList.size());
                if (onDone != null) onDone.run();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Data", "Failed to reload orders", error.toException());
                if (onDone != null) onDone.run();
            }
        });
    }

    // Link order to user name
    public void GetCustomerName(List<Order> orders) {
        for (Order order : orders) {
            User user = getUserById(order.getUser_id());
            if (user != null) {
                order.setCustomer_name(user.getName());
            } else {
                order.setCustomer_name("Unknown");
            }
        }
    }
    public interface OnUserRatingsLoadedListener {
        void onLoaded();
    }
}