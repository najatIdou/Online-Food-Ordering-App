package com.example.fooddeliveryapp.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fooddeliveryapp.Activity.DishDetailActivity;
import com.example.fooddeliveryapp.Adapter.FoodAdapter;
import com.example.fooddeliveryapp.Data.Data;
import com.example.fooddeliveryapp.Domain.Foods;
import com.example.fooddeliveryapp.R;

import java.util.List;

public class FoodListFragment extends Fragment {

    private List<Foods> foods;

    public static FoodListFragment newInstance(List<Foods> foods) {
        FoodListFragment fragment = new FoodListFragment();
        Bundle args = new Bundle();
        args.putSerializable("foods", (java.io.Serializable) foods);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            foods = (List<Foods>) getArguments().getSerializable("foods");
        }
    }

    // Create view
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_food_list, container, false);
        // Setup Recyclerview
        RecyclerView recyclerView = view.findViewById(R.id.foodRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Get duration
        Data data = Data.getInstance();
        java.util.Map<Integer, String> durationMap = data.getDurationMap();

        // Create adapter with click listener
        FoodAdapter adapter = new FoodAdapter(requireContext(), foods, food -> {
            Intent intent = new Intent(getActivity(), DishDetailActivity.class);
            intent.putExtra("foodId", food.getId());
            startActivity(intent);
        });
        adapter.setDurationMap(durationMap);
        recyclerView.setAdapter(adapter);
        return view;
    }
}