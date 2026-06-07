package com.example.luxevistaresort;

public class Review {
    public long id;
    public String reviewerName;
    public String reviewText;
    public int rating;
    public String reviewDate;
    public String reviewType; // "room" or "service"
    public long itemId; // room_id or service_id

    public Review(long id, String reviewerName, String reviewText, int rating, String reviewDate, String reviewType, long itemId) {
        this.id = id;
        this.reviewerName = reviewerName;
        this.reviewText = reviewText;
        this.rating = rating;
        this.reviewDate = reviewDate;
        this.reviewType = reviewType;
        this.itemId = itemId;
    }
}
