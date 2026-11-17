package com.example.fooddeliveryapp.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fooddeliveryapp.Adapter.AdminOrdersAdapter;
import com.example.fooddeliveryapp.Data.Data;
import com.example.fooddeliveryapp.Domain.Order;
import com.example.fooddeliveryapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class AdminOrdersFragment extends Fragment {

    private RecyclerView recyclerView;
    private AdminOrdersAdapter adapter;
    private DatabaseReference root;

    public AdminOrdersFragment() {}

    // Create view
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_orders, container, false);
        // Setup Recyclerview
        recyclerView = view.findViewById(R.id.ordersRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        Data.getInstance().setDataLoadedListener(() -> {
            if (getActivity() == null) return;
            // Get orders
            List<Order> orders = Data.getInstance().getOrders();
            adapter = new AdminOrdersAdapter(getContext(), orders, order -> {
                int orderId = order.getId();
                root = FirebaseDatabase.getInstance().getReference();

                // Delete order
                root.child("orders").child(String.valueOf(orderId)).removeValue();

                // Delete order items
                root.child("order_items").orderByChild("order_id").equalTo(orderId).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot snap : snapshot.getChildren()) {
                                    snap.getRef().removeValue();
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {}
                        });

                // Delete payments
                root.child("payment").orderByChild("order_id").equalTo(orderId).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot snap : snapshot.getChildren()) {
                                    snap.getRef().removeValue();
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {}
                        });
                // Delete restaurant ratings
                root.child("restaurant_ratings").orderByChild("order_id").equalTo(orderId).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot snap : snapshot.getChildren()) {
                                    snap.getRef().removeValue();
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {}
                        });
                // Delete food ratings
                root.child("food_ratings").orderByChild("order_id").equalTo(orderId).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot snap : snapshot.getChildren()) {
                                    snap.getRef().removeValue();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {}
                        });

                // Reload UI
                Data.getInstance().reloadOrders(() -> {
                    orders.clear();
                    orders.addAll(Data.getInstance().getOrders());
                    adapter.notifyDataSetChanged();
                    Toast.makeText(getContext(), "Order deleted", Toast.LENGTH_SHORT).show();
                });
            });

            recyclerView.setAdapter(adapter);
        });

        return view;
    }
}