package com.example.fooddeliveryapp.Domain;

public class Location {
    private int id;
    private String city;

    private String district;

    public Location() {
    }

    @Override
    public String toString() {
        return  city + " - " + district;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

}
