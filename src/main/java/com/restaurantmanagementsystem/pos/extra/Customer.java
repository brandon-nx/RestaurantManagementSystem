package com.restaurantmanagementsystem.pos.extra;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Customer {
    private String memberId;
    private String customerName;
    private String contactNumber;
    private String deliveryAddress;
    private List<Order> orderHistory;

    // Constructor
    public Customer(String memberId, String customerName, String contactNumber, String deliveryAddress) {
        this.memberId = memberId;
        this.customerName = customerName;
        this.contactNumber = contactNumber;
        this.deliveryAddress = deliveryAddress;
        this.orderHistory = new ArrayList<>();
    }

    // Getters and Setters
    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public List<Order> getOrderHistory() {
        return orderHistory;
    }

    public void setOrderHistory(String deliveryAddress) {
        this.orderHistory = orderHistory;
    }

    // Method to view order history
    public void viewOrderHistory() {
        if (orderHistory.isEmpty()) {
            System.out.println("-----------------------------------");
            System.out.println("No past orders found.");
            System.out.println("-----------------------------------");
            return;
        }

        System.out.println("-----------------------------------");
        System.out.println("Order History:");
        for (Order order : orderHistory) {
            System.out.println("Order at " + order.getRestaurant().getRestaurantName() + " - Total Cost: $" + String.format("%.2f", order.getTotalCost()));
        }
        System.out.println("-----------------------------------");
    }

    // Method to generate unique member Id
    public static String generateMemberId(Map<String, Customer> customerMap) {
        String memberId;
        Random random = new Random();
        do {
            int randomId = random.nextInt(99998) + 1; // Generate a random id between 00000 and 99999
            memberId = String.format("%05d", randomId);
        } while (customerMap.containsKey(memberId)); // Check for uniqueness

        return memberId;
    }

    // Method to check if name exists in customer map
    public static boolean isExistingMemberName(Map<String, Customer> customerMap, String name) {
        return customerMap.values().stream().anyMatch(member -> member.getCustomerName().equalsIgnoreCase(name));
    }

    // Method to save customers to a file
    public static void saveCustomers(String filename, Map<String, Customer> customerMap) {
        try (FileWriter writer = new FileWriter(filename)) {
            for (Customer customer : customerMap.values()) {
                writer.write(customer.getMemberId() + "," +
                        customer.getCustomerName() + "," +
                        customer.getContactNumber() + "," +
                        customer.getDeliveryAddress() + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // Method to load customers from a file
    public static void loadCustomers(String filename, Map<String, Customer> customerMap) {
        File myObj = new File(filename);
        try (Scanner myReader = new Scanner(myObj)) {
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                String[] parts = data.split(",");

                if (parts.length != 4) {
                    throw new IllegalArgumentException("Invalid data format for deserialization");
                }

                Customer customer = new Customer(parts[0], parts[1], parts[2], parts[3]);
                customerMap.put(customer.getMemberId(), customer);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}

