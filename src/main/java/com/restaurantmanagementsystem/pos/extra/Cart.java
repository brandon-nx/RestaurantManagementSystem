package com.restaurantmanagementsystem.pos.extra;

import java.util.ArrayList;
import java.util.List;

public class Cart implements IItemOperations{
    private List<CartItem> items;

    // Constructor
    public Cart() {
        this.items = new ArrayList<>();
    }


    // Getters and Setters
    public List<CartItem> getItems() {
        return items;
    }

    // Interface methods to add item into cart
    @Override
    public void addItem(Object item) {
        if (item instanceof CartItem) {
            items.add((CartItem) item);
        } else {
            // Optionally handle the error or log it
            System.out.println("Invalid item type. Only CartItem instances can be added.");
        }
    }

    // Interface methods to remove item from cart
    @Override
    public void removeItem(Object item) {
        if (item instanceof CartItem && items.contains(item)) {
            items.remove(item);
        } else {
            // Optionally handle the error or log it
            System.out.println("Invalid item type or item not found in the cart.");
        }
    }

    // Interface methods to update item in the cart
    @Override
    public void updateItem(Object oldItem, Object newItem) {
        if (oldItem instanceof CartItem && newItem instanceof CartItem) {
            int index = items.indexOf(oldItem);
            if (index != -1) {
                items.set(index, (CartItem) newItem);
            } else {
                // Optionally handle the error or log it
                System.out.println("Item to be updated not found in the cart.");
            }
        } else {
            // Optionally handle the error or log it
            System.out.println("Invalid item type for updating.");
        }
    }

    // Method to calculate total cost in the cart
    public double calculateTotalCost() {
        double totalCost = 0.0;
        for (CartItem cartItem : items) {
            totalCost += cartItem.getMenuItem().getPrice() * cartItem.getQuantity();
        }
        return totalCost;
    }

    // Method to display the cart contents
    public void displayCartContents() {
        if (items.isEmpty()) {
            System.out.println("-----------------------------------");
            System.out.println("Your cart is empty.");
            System.out.println("-----------------------------------");
            return;
        }

        System.out.println("-----------------------------------");
        System.out.println("Cart Contents:");
        for (CartItem item : items) {
            String formattedPrice = String.format("%.2f", item.getMenuItem().getPrice() * item.getQuantity());
            System.out.println(item.getQuantity() + " x " + item.getMenuItem().getItemName() + " - $" + formattedPrice);
        }
        System.out.println("Total Cost: $" + String.format("%.2f", calculateTotalCost()));
        System.out.println("-----------------------------------");
    }
}
