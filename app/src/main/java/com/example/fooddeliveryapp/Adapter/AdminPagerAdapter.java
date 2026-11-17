package com.example.fooddeliveryapp.Adapter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.fooddeliveryapp.Fragments.AdminOrdersFragment;
import com.example.fooddeliveryapp.Fragments.AdminUsersFragment;
import com.example.fooddeliveryapp.Fragments.AdminAnalyticsFragment;

// Swiping tabs
public class AdminPagerAdapter extends FragmentStateAdapter {

    // Constructor
    public AdminPagerAdapter(@NonNull AppCompatActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        // Returns fragment based on tab
        switch (position) {
            case 0: return new AdminOrdersFragment();
            case 1: return new AdminUsersFragment();
            case 2: return new AdminAnalyticsFragment();
            default: return new AdminOrdersFragment();
        }
    }

    // Total tabs
    @Override
    public int getItemCount() {
        return 3;
    }
}