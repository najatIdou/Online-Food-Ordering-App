package com.example.fooddeliveryapp.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fooddeliveryapp.Adapter.TopRestaurantsAdapter;
import com.example.fooddeliveryapp.Data.Data;
import com.example.fooddeliveryapp.Domain.Order;
import com.example.fooddeliveryapp.Domain.User;
import com.example.fooddeliveryapp.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AdminAnalyticsFragment extends Fragment {

    private static final String TAG = "AdminAnalytics";
    private TextView tvPending, tvCancelled, tvCompleted, tvRejected, tvAccepted, tvRevenue7Days,
            tvRevenue30Days, tvRevenueTotal, tvNewCustomers, tvNewRestaurants;
    private RecyclerView rvTopRestaurants;
    private TopRestaurantsAdapter topRestaurantsAdapter;

    public AdminAnalyticsFragment() {}

    // Create view
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_analytics, container, false);

        tvPending = view.findViewById(R.id.tvPending);
        tvCompleted = view.findViewById(R.id.tvCompleted);
        tvCancelled = view.findViewById(R.id.tvCancelled);
        tvAccepted = view.findViewById(R.id.tvAccepted);
        tvRejected = view.findViewById(R.id.tvRejected);

        tvRevenue7Days = view.findViewById(R.id.tvRevenueToday);
        tvRevenue30Days = view.findViewById(R.id.tvRevenueWeek);
        tvRevenueTotal = view.findViewById(R.id.tvRevenueMonth);

        tvNewCustomers = view.findViewById(R.id.tvNewCustomers);
        tvNewRestaurants = view.findViewById(R.id.tvNewRestaurants);

        rvTopRestaurants = view.findViewById(R.id.tvTopRestaurants);
        rvTopRestaurants.setLayoutManager(new LinearLayoutManager(getContext()));

        loadAnalytics();

        return view;
    }

    private void loadAnalytics() {
        Data data = Data.getInstance();
        if (data.getUsers() == null || data.getUsers().isEmpty()) {
            data.setDataLoadedListener(this::updateAnalytics);
        } else {
            updateAnalytics();
        }
    }

    private void updateAnalytics() {
        Data data = Data.getInstance();
        // Get data
        List<Order> orders = data.getOrders();
        List<User> users = data.getUsers();

        if (orders == null || users == null) return;

        Calendar now = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        // Order Status Counts
        double revenue7Days = 0, revenue30Days = 0, revenueTotal = 0;

        Calendar days7 = (Calendar) now.clone();
        days7.add(Calendar.DAY_OF_YEAR, -7);
        Calendar days30 = (Calendar) now.clone();
        days30.add(Calendar.DAY_OF_YEAR, -30);

        int completed = 0, pending = 0, accepted = 0, cancelled = 0, rejected = 0;

        for (Order order : orders) {
            if (order == null || order.getStatus() == null) continue;

            String status = order.getStatus().toLowerCase();

            // Count all statuses
            switch (status) {
                case "completed": completed++; break;
                case "pending": pending++; break;
                case "accepted": accepted++; break;
                case "cancelled": cancelled++; break;
                case "rejected": rejected++; break;
            }

            // Calc revenue for completed, pending, accepted
            if (status.equals("completed") || status.equals("pending") || status.equals("accepted")) {
                String createdAt = order.getCreated_at();
                if (createdAt == null) continue;
                try {
                    Calendar orderCal = Calendar.getInstance();
                    orderCal.setTime(sdf.parse(createdAt));
                    double price = order.getTotal();

                    revenueTotal += price;
                    if (!orderCal.before(days30)) revenue30Days += price;
                    if (!orderCal.before(days7)) revenue7Days += price;
                } catch (ParseException e) {
                    Log.e(TAG, "Failed to parse order date: " + createdAt, e);
                }
            }
        }

        // Display data
        tvCompleted.setText("Completed: " + completed);
        tvPending.setText("Pending: " + pending);
        tvAccepted.setText("Accepted: " + accepted);
        tvCancelled.setText("Cancelled: " + cancelled);
        tvRejected.setText("Rejected: " + rejected);

        tvRevenue7Days.setText(String.format(Locale.getDefault(), "Last 7 Days: $%.2f", revenue7Days));
        tvRevenue30Days.setText(String.format(Locale.getDefault(), "Last 30 Days: $%.2f", revenue30Days));
        tvRevenueTotal.setText(String.format(Locale.getDefault(), "Total: $%.2f", revenueTotal));

        // New Users last 7 days
        int newCustomers = 0;
        int newRestaurantOwners = 0;

        // Total counters
        int totalCustomers = 0;
        int totalRestaurantOwners = 0;

        Calendar sevenDaysAgo = (Calendar) now.clone();
        sevenDaysAgo.add(Calendar.DAY_OF_YEAR, -7);

        for (User user : users) {
            if (user == null || user.getCreated_at() == null || user.getRole() == null) continue;

            // Total counters
            if (user.getRole().equalsIgnoreCase("customer")) totalCustomers++;
            else if (user.getRole().equalsIgnoreCase("restaurant_owner")) totalRestaurantOwners++;

            // New users in last 7 days
            try {
                Calendar userCal = Calendar.getInstance();
                userCal.setTime(sdf.parse(user.getCreated_at()));

                if (!userCal.before(sevenDaysAgo)) {
                    if (user.getRole().equalsIgnoreCase("customer")) newCustomers++;
                    else if (user.getRole().equalsIgnoreCase("restaurant_owner")) newRestaurantOwners++;
                }
            } catch (ParseException e) {
                Log.e(TAG, "Failed to parse user date: " + user.getCreated_at(), e);
            }
        }

        tvNewCustomers.setText("New Customers (7 Days): " + newCustomers + " | Total: " + totalCustomers);
        tvNewRestaurants.setText("New Restaurants (7 Days): " + newRestaurantOwners + " | Total: " + totalRestaurantOwners);

        // Top restaurants by completed orders
        Map<String, Integer> restaurantOrderCount = new HashMap<>();
        for (Order order : orders) {
            if (order == null || !"completed".equalsIgnoreCase(order.getStatus())) continue;
            String restName = order.getRestaurant_name();
            if (restName != null && !restName.isEmpty()) {
                restaurantOrderCount.put(restName, restaurantOrderCount.getOrDefault(restName, 0) + 1);
            }
        }

        List<Map.Entry<String, Integer>> list = new ArrayList<>(restaurantOrderCount.entrySet());
        list.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue()));

        List<TopRestaurantsAdapter.RestaurantCount> topList = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : list) {
            topList.add(new TopRestaurantsAdapter.RestaurantCount(entry.getKey(), entry.getValue()));
        }

        topRestaurantsAdapter = new TopRestaurantsAdapter(getContext(), topList);
        rvTopRestaurants.setAdapter(topRestaurantsAdapter);
    }
}