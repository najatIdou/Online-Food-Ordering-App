package com.example.fooddeliveryapp.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fooddeliveryapp.Activity.AddFoodActivity;
import com.example.fooddeliveryapp.Adapter.MenuManageAdapter;
import com.example.fooddeliveryapp.Domain.Category;
import com.example.fooddeliveryapp.Domain.Foods;
import com.example.fooddeliveryapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RestaurantMenuFragment extends Fragment {

    private RecyclerView recyclerView;
    private MenuManageAdapter adapter;

    // Category names and foods
    private List<Object> items = new ArrayList<>();
    private int restaurantId = -1;
    private Button addButton;
    private DatabaseReference catRef, foodsRef;

    // Create view
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_restaurant_menu, container, false);
        // Setup Recyclerview
        recyclerView = view.findViewById(R.id.recyclerView);
        addButton = view.findViewById(R.id.btnAddFood);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Get restauid
        if (getArguments() != null) {
            restaurantId = getArguments().getInt("restaurantId", -1);
        }

        // Add food button
        addButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddFoodActivity.class);
            intent.putExtra("restaurantId", restaurantId);
            startActivity(intent);
        });

        loadMenu();
        return view;
    }

    private void loadMenu() {
        if (restaurantId == -1) return;

        // Reference to foods table in firebase
        foodsRef = FirebaseDatabase.getInstance().getReference("foods");
        foodsRef.orderByChild("restaurant_id").equalTo(restaurantId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Map<Integer, List<Foods>> foodsByCategory = new HashMap<>();
                        for (DataSnapshot foodSnap : snapshot.getChildren()) {
                            Foods food = foodSnap.getValue(Foods.class);
                            if (food != null) {
                                // Group foods by category
                                int catId = food.getCategory_id();
                                if (!foodsByCategory.containsKey(catId)) {
                                    foodsByCategory.put(catId, new ArrayList<>());
                                }
                                foodsByCategory.get(catId).add(food);
                            }
                        }

                        // Get category names
                        catRef = FirebaseDatabase.getInstance().getReference("categories");
                        catRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot catSnapshot) {
                                Map<Integer, String> catNames = new HashMap<>();
                                for (DataSnapshot catSnap : catSnapshot.getChildren()) {
                                    Category cat = catSnap.getValue(Category.class);
                                    if (cat != null) {
                                        catNames.put(cat.getId(), cat.getName());
                                    }
                                }

                                items.clear();
                                for (Map.Entry<Integer, List<Foods>> entry : foodsByCategory.entrySet()) {
                                    String catName = catNames.get(entry.getKey());
                                    if (catName == null) catName = "Other";
                                    items.add(catName);
                                    items.addAll(entry.getValue());
                                }

                                // Manage menu
                                adapter = new MenuManageAdapter(requireContext(), items, new MenuManageAdapter.OnItemClickListener() {
                                            @Override
                                            public void onEditClick(Foods food) {
                                                // Edit food
                                                Intent intent = new Intent(getActivity(), AddFoodActivity.class);
                                                intent.putExtra("foodId", food.getId());
                                                startActivity(intent);
                                            }

                                            @Override
                                            public void onDeleteClick(Foods food) {
                                                // Delete food
                                                deleteFood(food);
                                            }
                                        });
                                recyclerView.setAdapter(adapter);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(getContext(), "Failed to load categories", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), "Failed to load menu", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void deleteFood(Foods food) {
        new AlertDialog.Builder(requireContext()).setTitle("Delete Item").setMessage("Are you sure you want to delete " + food.getName() + "?").setPositiveButton("Yes", (dialog, which) -> {

                    // Reference foods table in firebase
                    foodsRef = FirebaseDatabase.getInstance().getReference("foods").child(String.valueOf(food.getId()));
                    foodsRef.removeValue().addOnSuccessListener(aVoid -> {
                                Toast.makeText(getContext(), "Deleted", Toast.LENGTH_SHORT).show();
                                // Refresh
                                loadMenu();
                            })
                            .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                })
                .setNegativeButton("No", null)
                .show();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh
        loadMenu();
    }
}