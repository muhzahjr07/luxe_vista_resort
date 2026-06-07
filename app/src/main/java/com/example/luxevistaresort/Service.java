package com.example.luxevistaresort;

public class Service {
    public long id;
    public String name, description, availability, duration, category;
    public double price;

    public Service(long id, String name, String description, String availability, double price, String duration, String category) {
        this.id = id; 
        this.name = name; 
        this.description = description;
        this.availability = availability; 
        this.price = price;
        this.duration = duration;
        this.category = category;
    }

    public Service(long id, String name, String description, String availability, double price) {
        this(id, name, description, availability, price, "", "");
    }
}
