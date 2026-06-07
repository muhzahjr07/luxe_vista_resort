package com.example.luxevistaresort;

public class Room {
    public long id;
    public String name;
    public String description;
    public double price;
    public String image;
    public String amenities;
    public int capacity;

    public Room(long id, String name, String description, double price, String image, String amenities, int capacity) {
        this.id = id; 
        this.name = name; 
        this.description = description;
        this.price = price; 
        this.image = image;
        this.amenities = amenities;
        this.capacity = capacity;
    }

    public Room(long id, String name, String description, double price, String image) {
        this(id, name, description, price, image, "", 2);
    }
}
