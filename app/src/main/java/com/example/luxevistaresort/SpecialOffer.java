package com.example.luxevistaresort;

public class SpecialOffer {
    public String icon;
    public String title;
    public String discount;
    public String description;
    public String originalPrice;
    public String discountedPrice;
    public String validUntil;
    public String offerType; // "room", "service", "package"

    public SpecialOffer(String icon, String title, String discount, String description, 
                       String originalPrice, String discountedPrice, String validUntil, String offerType) {
        this.icon = icon;
        this.title = title;
        this.discount = discount;
        this.description = description;
        this.originalPrice = originalPrice;
        this.discountedPrice = discountedPrice;
        this.validUntil = validUntil;
        this.offerType = offerType;
    }
}
