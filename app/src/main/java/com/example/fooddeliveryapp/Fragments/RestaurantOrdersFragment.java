package com.example.fooddeliveryapp.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fooddeliveryapp.Adapter.RestaurantOrdersAdapter;
import com.example.fooddeliveryapp.Domain.Address;
import com.example.fooddeliveryapp.Domain.Order;
import com.example.fooddeliveryapp.Domain.User;
import com.example.fooddeliveryapp.R;
import com.google.firebase.database.*;

import java.util.*;

public class RestaurantOrdersFragment extends Fragment {

    private static final String TAG = "RestaurantOrdersFrag";

    private TextView tvPendingTitle, tvAcceptedTitle, tvRejectedTitle, tvCompletedTitle, tvNoOrders;
    private RecyclerView recyclerPending, recyclerAccepted, recyclerRejected, recyclerCompleted;

    private RestaurantOrdersAdapter pendingAdapter, acceptedAdapter, rejectedAdapter, completedAdapter;

    private List<Order> pendingOrders = new ArrayList<>();
    private List<Order> acceptedOrders = new ArrayList<>();
    private List<Order> rejectedOrders = new ArrayList<>();
    private OnOrdersChangedListener ordersChangedListener;
    private List<Order> completedOrders = new ArrayList<>();

    private int restaurantId = -1;

    private Map<Integer, User> userMap = new HashMap<>();
    private Map<Integer, Address> addressMap = new HashMap<>();

    private Query ordersQuery;
    private ValueEventListener ordersListener;
    private DatabaseReference usersRef, addressRef;


    public interface OnOrdersChangedListener {
        void onOrdersChanged();
    }

    public void setOrdersChangedListener(OnOrdersChangedListener listener) {
        this.ordersChangedListener = listener;
    }

    // Create view
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_restaurant_orders, container, false);

        tvPendingTitle = view.findViewById(R.id.tvPendingTitle);
        tvAcceptedTitle = view.findViewById(R.id.tvAcceptedTitle);
        tvRejectedTitle = view.findViewById(R.id.tvRejectedTitle);
        tvCompletedTitle = view.findViewById(R.id.tvCompletedTitle);
        tvNoOrders = view.findViewById(R.id.tvNoOrders);
        recyclerPending = view.findViewById(R.id.recyclerPending);
        recyclerAccepted = view.findViewById(R.id.recyclerAccepted);
        recyclerRejected = view.findViewById(R.id.recyclerRejected);
        recyclerCompleted = view.findViewById(R.id.recyclerCompleted);

        recyclerPending.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerAccepted.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerRejected.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerCompleted.setLayoutManager(new LinearLayoutManager(getContext()));

        if (getArguments() != null) {
            restaurantId = getArguments().getInt("restaurantId", -1);
        }

        if (restaurantId == -1) {
            Toast.makeText(getContext(), "Restaurant ID not found", Toast.LENGTH_SHORT).show();
            return view;
        }

        loadUsersAndAddresses();
        return view;
    }

    // Load data
    private void loadUsersAndAddresses() {

        // Reference to user table in firebase
        usersRef = FirebaseDatabase.getInstance().getReference("users");
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snap : snapshot.getChildren()) {
                    User user = snap.getValue(User.class);
                    if (user != null) userMap.put(user.getId(), user);
                }
                loadAddresses();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                loadAddresses();
            }
        });
    }

    // Load address
    private void loadAddresses() {

        // Reference to address table in firebase
        addressRef  = FirebaseDatabase.getInstance().getReference("addresses");
        addressRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snap : snapshot.getChildren()) {
                    Address address = snap.getValue(Address.class);
                    if (address != null) addressMap.put(address.getId(), address);
                }
                startListeningForOrders();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                startListeningForOrders();
            }
        });
    }

    private void startListeningForOrders() {
        // Get restau orders
        ordersQuery = FirebaseDatabase.getInstance().getReference("orders").orderByChild("restaurant_id").equalTo(restaurantId);
        ordersListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                pendingOrders.clear();
                acceptedOrders.clear();
                rejectedOrders.clear();
                completedOrders.clear();

                for (DataSnapshot snap : snapshot.getChildren()) {
                    Order order = snap.getValue(Order.class);

                    if (order != null) {
                        enrichOrder(order);

                        // Split orders
                        switch (order.getStatus()) {
                            case Order.STATUS_PENDING:
                                pendingOrders.add(order);
                                break;
                            case Order.STATUS_ACCEPTED:
                                acceptedOrders.add(order);
                                break;
                            case Order.STATUS_REJECTED:
                                rejectedOrders.add(order);
                                break;
                            case Order.STATUS_COMPLETED:
                                completedOrders.add(order);
                                break;
                        }
                    }
                }
                setupAdapters();
                updateVisibility();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load orders", Toast.LENGTH_SHORT).show();
            }
        };

        ordersQuery.addValueEventListener(ordersListener);
    }

    private void enrichOrder(Order order) {
        User user = userMap.get(order.getUser_id());

        // Add customer name, phone, address
        if (user != null) {
            order.setCustomer_name(user.getName());
            order.setCustomer_phone(user.getPhone());
        }
        Address addr = addressMap.get(order.getDelivery_address_id());
        if (addr != null) {
            order.setDelivery_address(addr.getAddress_text());
        }
    }

    // Actions on order
    private void setupAdapters() {
        // Pending
        pendingAdapter = new RestaurantOrdersAdapter(getContext(), pendingOrders, true,
                new RestaurantOrdersAdapter.OnOrderActionListener() {
                    @Override
                    public void onAccept(Order order) {
                        updateOrderStatus(order, Order.STATUS_ACCEPTED);
                    }
                    @Override
                    public void onReject(Order order) {
                        updateOrderStatus(order, Order.STATUS_REJECTED);
                    }
                    @Override
                    public void onComplete(Order order) {}
                });
        recyclerPending.setAdapter(pendingAdapter);

        // Accepted
        acceptedAdapter = new RestaurantOrdersAdapter(getContext(), acceptedOrders, false,
                new RestaurantOrdersAdapter.OnOrderActionListener() {

                    @Override public void onAccept(Order order) {}
                    @Override public void onReject(Order order) {}

                    @Override
                    public void onComplete(Order order) {

                        // Firebase update
                        updateOrderStatus(order, Order.STATUS_COMPLETED);

                        acceptedOrders.removeIf(o -> o.getId() == order.getId());
                        completedOrders.add(order);

                        acceptedAdapter.notifyDataSetChanged();
                        completedAdapter.notifyDataSetChanged();

                        // Update titles
                        tvAcceptedTitle.setText("Accepted Orders (" + acceptedOrders.size() + ")");
                        tvCompletedTitle.setText("Completed Orders (" + completedOrders.size() + ")");
                    }
                });

        recyclerAccepted.setAdapter(acceptedAdapter);

        // Rejected
        rejectedAdapter = new RestaurantOrdersAdapter(getContext(), rejectedOrders, false, null);
        recyclerRejected.setAdapter(rejectedAdapter);

        // Completed
        completedAdapter = new RestaurantOrdersAdapter(getContext(), completedOrders, false, null);
        recyclerCompleted.setAdapter(completedAdapter);

        // Titles
        tvPendingTitle.setText("Pending Orders (" + pendingOrders.size() + ")");
        tvAcceptedTitle.setText("Accepted Orders (" + acceptedOrders.size() + ")");
        tvRejectedTitle.setText("Rejected Orders (" + rejectedOrders.size() + ")");
        tvCompletedTitle.setText("Completed Orders (" + completedOrders.size() + ")");
    }

    // Update status
    private void updateOrderStatus(Order order, String newStatus) {
        FirebaseDatabase.getInstance().getReference("orders").child(String.valueOf(order.getId())).child("status").setValue(newStatus).addOnSuccessListener(a ->
                        Toast.makeText(getContext(), "Order " + newStatus, Toast.LENGTH_SHORT).show()
                )
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }

    // No orders
    private void updateVisibility() {
        boolean hasOrders = !pendingOrders.isEmpty() || !acceptedOrders.isEmpty() || !rejectedOrders.isEmpty() || !completedOrders.isEmpty();
        tvNoOrders.setVisibility(hasOrders ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (ordersQuery != null && ordersListener != null) {
            ordersQuery.removeEventListener(ordersListener);
        }
    }
}