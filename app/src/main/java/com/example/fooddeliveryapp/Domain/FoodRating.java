package com.example.fooddeliveryapp.Domain;

public class FoodRating {
    private int id;
    private int food_id;
    private int user_id;
    private int order_id;
    private float rating;
    private String created_at;

    public FoodRating() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getFood_id() { return food_id; }
    public void setFood_id(int food_id) { this.food_id = food_id; }

    public int getUser_id() { return user_id; }
    public void setUser_id(int user_id) { this.user_id = user_id; }

    public int getOrder_id() { return order_id; }
    public void setOrder_id(int order_id) { this.order_id = order_id; }

    public float getRating() { return rating; }
    public void setRating(float rating) { this.rating = rating; }

    public String getCreated_at() { return created_at; }
    public void setCreated_at(String created_at) { this.created_at = created_at; }
}