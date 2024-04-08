package com.restaurantmanagementsystem.pos.extra;

import java.util.List;

public class SpecialOffer {
    private String offerDescription;
    private double discount;

    // Constructor
    public SpecialOffer(String offerDescription, double discount) {
        this.offerDescription = offerDescription;
        this.discount = discount;
    }

    // Getters and Setters
    public String getOfferDescription() {
        return offerDescription;
    }
    public void setOfferDescription(String offerDescription) {
        this.offerDescription = offerDescription;
    }
    public double getDiscount() {
        return discount;
    }
    public void setDiscount(double discount) {
        this.discount = discount;
    }

    // Method to apply the offer
    public double applyOffer(double cost) {
        return cost - (cost * (discount / 100.0));
    }

    // Method to display the offer
    public void displayOffers(List<Restaurant> restaurants) {
        System.out.println("Current Special Offers:");
        boolean hasOffers = false;
        for (Restaurant restaurant : restaurants) {
            for (SpecialOffer offer : restaurant.getSpecialOffers()) {
                System.out.println("- " + restaurant.getRestaurantName() + ": " + offer.getOfferDescription());
                hasOffers = true;
            }
        }
        if (!hasOffers) {
            System.out.println("No special offers available at the moment.");
        }
        System.out.println("-----------------------------------");
    }
}
