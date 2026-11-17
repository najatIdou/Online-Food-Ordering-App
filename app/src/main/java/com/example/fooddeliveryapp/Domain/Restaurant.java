package com.example.fooddeliveryapp.Domain;

public class Restaurant {
    private int id;
    private int owner_id;
    private String name;
    private String address;
    private String image;
    private int is_open;
    private int location_id;

    public Restaurant() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getOwner_id() { return owner_id; }
    public void setOwner_id(int owner_id) { this.owner_id = owner_id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public int getIs_open() { return is_open; }
    public void setIs_open(int is_open) { this.is_open = is_open; }

    public int getLocation_id() { return location_id; }
    public void setLocation_id(int location_id) { this.location_id = location_id; }
}