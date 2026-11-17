package com.example.fooddeliveryapp.Utils;

import com.example.fooddeliveryapp.Data.Cart;

import java.util.List;

// Cart items group of a restau
public class CartGroup {
    private int restaurantId;
    private String restaurantName;

    // Item list in cart
    private List<Cart.CartItem> items;

    // Constructor
    public CartGroup(int restaurantId, String restaurantName, List<Cart.CartItem> items) {
        this.restaurantId = restaurantId;
        this.restaurantName = restaurantName;
        this.items = items;
    }

    // Getters
    public String getRestaurantName() { return restaurantName; }
    public List<Cart.CartItem> getItems() { return items; }
}