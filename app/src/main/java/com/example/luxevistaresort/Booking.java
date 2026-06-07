package com.example.luxevistaresort;

public class Booking {
    public long id;
    public String name;
    public String date1; // check-in date for rooms, booking date for services
    public String date2; // check-out date for rooms, booking time for services
    public double price;
    public String type; // "room" or "service"

    public Booking(long id, String name, String date1, String date2, double price, String type) {
        this.id = id;
        this.name = name;
        this.date1 = date1;
        this.date2 = date2;
        this.price = price;
        this.type = type;
    }
}
