package com.example.fooddeliveryapp.Domain;

public class RestaurantRating {

    private int restaurant_id;
    private int user_id;
    private float rating;
    private String comment;

    public RestaurantRating() {}

    public int getRestaurant_id() { return restaurant_id; }
    public void setRestaurant_id(int restaurant_id) { this.restaurant_id = restaurant_id; }

    public int getUser_id() { return user_id; }
    public void setUser_id(int user_id) { this.user_id = user_id; }

    public float getRating() { return rating; }
    public void setRating(float rating) { this.rating = rating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
}