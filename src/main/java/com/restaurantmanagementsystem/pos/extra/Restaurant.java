package com.restaurantmanagementsystem.pos.extra;

import java.util.*;

public class Restaurant {
    private String restaurantName;
    private List<MenuItem> menu;
    private List<SpecialOffer> specialOffers;
    private Map<MenuItem, Integer> stock;


    // Constructor
    public Restaurant(String restaurantName) {
        this.restaurantName = restaurantName;
        this.menu = new ArrayList<>();
        this.specialOffers = new ArrayList<>();
        this.stock = new HashMap<>();
    }


    // Getters and Setters
    public String getRestaurantName() {
        return restaurantName;
    }
    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public List<MenuItem> getMenu() {
        return menu;
    }
    public void setMenu(List<MenuItem> menu) {
        this.menu = menu;
    }

    public List<SpecialOffer> getSpecialOffers() {
        return specialOffers;
    }
    public void setSpecialOffers(List<SpecialOffer> specialOffers) {
        this.specialOffers = specialOffers;
    }

    public Map<MenuItem, Integer> getStock() {
        return stock;
    }
    public void setStock(Map<MenuItem, Integer> stock) {
        this.stock = stock;
    }

    // Method to add item to menu
    public void addToMenu(MenuItem menuItem, int initialStock) {
        menu.add(menuItem);
        updateInventory(menuItem, initialStock);
    }

    // Method to remove item from the menu
    public void removeFromMenu(String itemName) {
        menu.removeIf(item -> item.getItemName().equalsIgnoreCase(itemName));
    }

    // Method to add special offer to menu
    public void addSpecialOffer(SpecialOffer specialOffer) {
        specialOffers.add(specialOffer);
    }

    // Method to process an order
    public Order processOrder(Customer customer, Cart cart) {
        Order order = new Order(customer, this, cart.getItems(), specialOffers);

        if (canFulfillOrder(order)) {
            try {
                // Deduct items from inventory
                for (CartItem item : order.getItems()) {
                    updateInventory(item.getMenuItem(), -item.getQuantity());
                }

                // Display total cost
                System.out.println("Order confirmed! Total cost: $" + String.format("%.2f", order.getTotalCost()));
                System.out.println("-----------------------------------");
                return order;

            } catch (IllegalArgumentException e) {
                System.out.println("Error processing order: " + e.getMessage());
                System.out.println("-----------------------------------");
                return null;
            }
        } else {
            System.out.println("Unable to process the order due to insufficient stock.");
            System.out.println("-----------------------------------");
            return null;
        }
    }

    // Method to check if the order can be fulfilled based on inventory
    private boolean canFulfillOrder(Order order) {
        for (CartItem item : order.getItems()) {
            if (!isItemAvailable(item.getMenuItem(), item.getQuantity())) {
                return false;
            }
        }
        return true;
    }

    // Method to check if the availability of the item
    public boolean isItemAvailable(MenuItem item, int quantity) {
        return stock.getOrDefault(item, 0) >= quantity;
    }

    // Method to update the inventory
    public void updateInventory(MenuItem item, int quantity) {
        int currentStock = stock.getOrDefault(item, 0);
        int newStock = currentStock + quantity;
        if (newStock < 0) {
            throw new IllegalArgumentException("Cannot have negative stock.");
        }
        stock.put(item, newStock);
    }

    // Method to display list of restaurants
    public static void displayRestaurants(List<Restaurant> restaurants) {
        System.out.println("Please choose a restaurant or exit:");
        for (int i = 0; i < restaurants.size(); i++) {
            System.out.println((i + 1) + ". " + restaurants.get(i).getRestaurantName());
        }
        System.out.println("-----------------------------------");
    }

    // Method to get the selected restaurant choice and its menu or exit
    public static Restaurant getRestaurantChoiceAndMenu(Integer option, List<Restaurant> restaurants) {
        if (option > 0 && option <= restaurants.size()) {
            Restaurant selectedRestaurant = restaurants.get(option - 1);

            System.out.println("Welcome to " + selectedRestaurant.getRestaurantName() + "!");
            System.out.println("-----------------------------------");
            System.out.println("Menu:");
            for (MenuItem menuItem : selectedRestaurant.getMenu()) {
                System.out.println(menuItem.getItemName() + " - RM" + menuItem.getPrice());
            }
            System.out.println("-----------------------------------");

            return selectedRestaurant;
        } else {
            System.out.println("Invalid choice, please try again.");
            System.out.println("-----------------------------------");
        }
        return null;
    }
}
