package com.restaurantmanagementsystem.pos.extra;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class FoodOrderingSystem {
    private SpecialOffer specialOffer;
    private Customer customer;
    private MenuItem menuItem;
    private Map<String, Customer> customerMap;
    private List<Restaurant> restaurants;
    private Scanner userInput;

    public FoodOrderingSystem() {
        // Initialise
        userInput = new Scanner(System.in);
        restaurants = new ArrayList<>();
        customerMap = new HashMap<>();
        Customer.loadCustomers("customers.txt", customerMap);

        // Add restaurants
        restaurants.add(new Restaurant("Yummy Restaurant"));
        restaurants.add(new Restaurant("Delicious Restaurant"));

        // Add items to the menu of the restaurants
        restaurants.get(0).addToMenu(new FoodItem("Cheese Burger", 9.90, "Delicious cheesy chicken burger with pickle inside", "American"), 10);
        restaurants.get(0).addToMenu(new FoodItem("Chicken Chop", 14.90, "Delicious chicken chop sides with wedges and salad", "Western"), 10);
        restaurants.get(0).addToMenu(new DrinkItem("Cola", 2.90, "Refreshing cola drink", "Soft Drink"), 20);

        restaurants.get(1).addToMenu(new FoodItem("Carbonara Pasta", 12.90, "Delicious creamy cheesy spaghetti with chicken slices", "Italian"), 10);
        restaurants.get(1).addToMenu(new DrinkItem("Sprite", 2.90, "Refreshing cola drink", "Soft Drink"), 20);
        restaurants.get(1).addToMenu(new DrinkItem("Ice Lemon Tea", 2.90, "Refreshing cola drink", "Soft Drink"), 20);

        // Add special offers
        specialOffer = new SpecialOffer("10% Off on All Soft Drink", 10.0);
        restaurants.get(1).addSpecialOffer(specialOffer);
    }

    // 1. Register as a New Library Member
    private void registerMember(){
        System.out.print("Enter your name: ");
        String name = userInput.nextLine();

        System.out.print("Enter your contact number: ");
        String contactNumber = userInput.nextLine();

        System.out.print("Enter your delivery address: ");
        String deliveryAddress = userInput.nextLine();

        // Generate random but unique member ID
        String memberId = Customer.generateMemberId(customerMap);

        // Check if a member with the entered name already exists
        if (Customer.isExistingMemberName(customerMap, name)) {
            System.out.println("A member with the name '" + name + "' already exists. Please enter a different name.");
        } else {
            customer = new Customer(memberId, name, contactNumber, deliveryAddress);
            customerMap.put(memberId, customer);
            Customer.saveCustomers("customers.txt", customerMap);

            System.out.println("-----------------------------------");
            System.out.println(name + ", you are now registered as a library member with ID: " + memberId);
            System.out.println("-----------------------------------");
        }
    }


    // 2. Place Food Order
    private void placeOrder() {
        System.out.print("Enter your member ID: ");
        String memberId = userInput.nextLine();
        System.out.println("-----------------------------------");
        customer = customerMap.get(memberId);

        if (!customerMap.containsKey(memberId)) {
            System.out.println("Invalid member ID. Please try again.");
            System.out.println("-----------------------------------");
        } else {
            // Display list of restaurants available
            Restaurant.displayRestaurants(restaurants);

            // Prompt the user to choose restaurant and display its menu
            int option;
            Restaurant selectedRestaurant;
            boolean validSelection = false;

            do {
                try {
                    System.out.print("Enter your choice: ");
                    option = userInput.nextInt();
                    userInput.nextLine();
                    System.out.println("-----------------------------------");

                    selectedRestaurant = Restaurant.getRestaurantChoiceAndMenu(option, restaurants);

                    // Check if a valid restaurant has been selected
                    if (selectedRestaurant != null) {
                        validSelection = true;

                        // Create a new cart and order for the current customer
                        Cart cart = new Cart();

                        // Prompt the user to choose which item to order
                        boolean isOrdering = true;
                        while (isOrdering) {
                            System.out.println("Enter the name of the item you want to order (type 'done' to finish, 'view' to view cart):");
                            String inputOrder = userInput.nextLine();

                            switch (inputOrder.toLowerCase()) {
                                case "done":
                                    isOrdering = false;
                                    if (cart.getItems().isEmpty()) {
                                        System.out.println("-----------------------------------");
                                        System.out.println("Your cart is empty. No charges applied.");
                                        System.out.println("-----------------------------------");
                                    } else {
                                        Order.displayOrderSummary(cart);

                                        System.out.print("Please confirm your order (Y/N): ");
                                        String confirmOrder = userInput.nextLine().toUpperCase();
                                        System.out.println("-----------------------------------");

                                        if (confirmOrder.equals("Y")) {
                                            Order newOrder = selectedRestaurant.processOrder(customer, cart);
                                            if (newOrder != null) {
                                                customer.getOrderHistory().add(newOrder);
                                            }
                                        } else if (confirmOrder.equals("N")) {
                                            System.out.println("Order cancelled... ");
                                            System.out.println("Returning to main page... ");
                                            System.out.println("-----------------------------------");
                                        }
                                    }
                                    break;
                                case "view":
                                    cart.displayCartContents();
                                    break;
                                default:
                                    menuItem = MenuItem.findMenuItem(selectedRestaurant, inputOrder);
                                    if (menuItem != null) {
                                        System.out.print("Enter quantity: ");
                                        int inputQuantity = userInput.nextInt();
                                        userInput.nextLine();

                                        menuItem.handleMenuItemSelection(selectedRestaurant, cart, menuItem, inputOrder, inputQuantity);
                                    } else {
                                        System.out.println("-----------------------------------");
                                        System.out.println("Item not available or not found.");
                                        System.out.println("-----------------------------------");
                                    }
                            }
                        }
                    } else {
                        break;
                    }
                } catch (InputMismatchException e) {
                    System.out.println("Invalid input. Please enter a valid number.");
                    System.out.println("-----------------------------------");
                    userInput.nextLine();
                }
            } while (!validSelection);
        }
    }



    // 3. View Order History
    private void orderHistory() {
        System.out.print("Enter your member ID: ");
        String memberId = userInput.nextLine();
        customer = customerMap.get(memberId);

        if (!customerMap.containsKey(memberId)) {
            System.out.println("-----------------------------------");
            System.out.println("Invalid member ID. Please try again.");
            System.out.println("-----------------------------------");
        } else {
            Customer customer = customerMap.get(memberId);
            customer.viewOrderHistory();
        }
    }


    // 4. Admin Login
    private void adminLogin() {
        System.out.print("Enter admin username: ");
        String username = userInput.nextLine();
        System.out.print("Enter admin password: ");
        String password = userInput.nextLine();

        if (Admin.checkAdminCredentials(username, password)) {
            Admin admin = new Admin(restaurants, userInput);
            admin.showAdminMenu();
        } else {
            System.out.println("-----------------------------------");
            System.out.println("Invalid admin credentials. Please try again.");
            System.out.println("-----------------------------------");
        }
    }

    
    public void run() {
        try {
            int option = -1;
            do {
                displayMenu();
                try {
                    option = userInput.nextInt();
                    System.out.println("-----------------------------------");
                    userInput.nextLine();
                } catch (InputMismatchException e) {
                    System.out.println("-----------------------------------");
                    System.out.println("Invalid input. Please enter a number.");
                    System.out.println("-----------------------------------");
                    userInput.nextLine();
                    continue;
                }

                switch (option) {
                    case 1:
                        registerMember();
                        break;
                    case 2:
                        placeOrder();
                        break;
                    case 3:
                        orderHistory();
                        break;
                    case 4:
                        adminLogin();
                        break;
                    case 5:
                        System.out.println("Thank you for using FoodieBran. Goodbye !");
                        System.out.println("-----------------------------------");
                        break;
                    default:
                        System.out.println("Invalid option. Please try again (1-5).");
                        System.out.println("-----------------------------------");
                        break;
                }
            } while (option != 5);
        } catch (Exception e) {
            System.out.println("An unexpected error occurred: " + e.getMessage());
        }
    }

    private void displayMenu() {
        System.out.println("Welcome to FoodieBran!");
        System.out.println("-----------------------------------");
        specialOffer.displayOffers(restaurants);
        System.out.print("""
            1. Register as a New Member to get Exclusive Discount
            2. Place Food Order
            3. View Order History
            4. Admin Login
            5. Exit
            -----------------------------------
            Enter your choice:""");
    }

    public static void main(String[] args) {
        FoodOrderingSystem foodOrderingSystem = new FoodOrderingSystem();
        foodOrderingSystem.run();
    }
}