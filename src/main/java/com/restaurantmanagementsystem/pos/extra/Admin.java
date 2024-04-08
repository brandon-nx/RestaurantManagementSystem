package com.restaurantmanagementsystem.pos.extra;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Scanner;

public class Admin implements IManageData {
    private List<Restaurant> restaurants;
    private Scanner userInput;

    // Constructor
    public Admin(List<Restaurant> restaurants, Scanner userInput) {
        this.restaurants = restaurants;
        this.userInput = userInput;
    }

    // Interface method to add restaurant and menu
    @Override
    public void addData(Object data) {
        if (data instanceof Restaurant) {
            Restaurant newRestaurant = (Restaurant) data;
            this.restaurants.add(newRestaurant);
            System.out.println("-----------------------------------");
            System.out.println("Restaurant added successfully.");
        } else {
            System.out.println("-----------------------------------");
            System.out.println("Invalid data type. Can only add Restaurant objects.");
        }
    }

    // Interface method to remove restaurant and menu
    @Override
    public void removeData(Object data) {
        if (data instanceof Restaurant) {
            Restaurant restaurantToRemove = (Restaurant) data;
            if (this.restaurants.contains(restaurantToRemove)) {
                this.restaurants.remove(restaurantToRemove);
                System.out.println("-----------------------------------");
                System.out.println("Restaurant removed successfully.");
            } else {
                System.out.println("-----------------------------------");
                System.out.println("Restaurant not found.");
            }
        } else {
            System.out.println("-----------------------------------");
            System.out.println("Invalid data type. Can only remove Restaurant objects.");
        }
    }

    // Interface method to update restaurant and menu
    @Override
    public void updateData(Object oldData, Object newData) {
        if (oldData instanceof Restaurant && newData instanceof Restaurant) {
            int index = this.restaurants.indexOf((Restaurant) oldData);
            if (index != -1) {
                this.restaurants.set(index, (Restaurant) newData);
                System.out.println("-----------------------------------");
                System.out.println("Restaurant data updated successfully.");
            } else {
                System.out.println("-----------------------------------");
                System.out.println("Restaurant not found.");
            }
        } else {
            System.out.println("-----------------------------------");
            System.out.println("Invalid data type. Can only update Restaurant objects.");
        }
    }

    // Method to check admin credentials
    public static boolean checkAdminCredentials(String username, String password) {
        try {
            File file = new File("admin.txt");
            Scanner scanner = new Scanner(file);

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] credentials = line.split(",");

                if (credentials.length == 2) {
                    String storedUsername = credentials[0].trim();
                    String storedPassword = credentials[1].trim();

                    if (username.equals(storedUsername) && password.equals(storedPassword)) {
                        return true;
                    }
                }
            }

            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return false;
    }

    // Admin: 1. Add Data
    private void insertData() {
        System.out.print("Do you want to add a new Restaurant (R) or a new Menu Item (M)? (R/M): ");
        String choice = userInput.nextLine().toUpperCase();

        if (choice.equals("R")) {
            System.out.print("Enter the name of the new restaurant: ");
            String restaurantName = userInput.nextLine();
            Restaurant newRestaurant = new Restaurant(restaurantName);
            addData(newRestaurant);
        } else if (choice.equals("M")) {
            Restaurant.displayRestaurants(restaurants);
            System.out.print("Enter the restaurant index to add menu item: ");
            int restaurantIndex = userInput.nextInt() - 1;
            userInput.nextLine();

            if (restaurantIndex >= 0 && restaurantIndex < restaurants.size()) {
                Restaurant selectedRestaurant = restaurants.get(restaurantIndex);

                System.out.print("Enter item name: ");
                String itemName = userInput.nextLine();
                System.out.print("Enter item price: ");
                double itemPrice = userInput.nextDouble();
                userInput.nextLine();
                System.out.print("Enter item description: ");
                String itemDescription = userInput.nextLine();
                System.out.print("Is this a Food item (F) or a Drink item (D)? (F/D): ");
                String itemType = userInput.nextLine().toUpperCase();

                MenuItem menuItem = null;
                if (itemType.equals("F")) {
                    System.out.print("Enter cuisine type: ");
                    String cuisineType = userInput.nextLine();
                    menuItem = new FoodItem(itemName, itemPrice, itemDescription, cuisineType);
                } else if (itemType.equals("D")) {
                    System.out.print("Enter beverage type: ");
                    String beverageType = userInput.nextLine();
                    menuItem = new DrinkItem(itemName, itemPrice, itemDescription, beverageType);
                }

                System.out.print("Enter initial stock quantity for this item: ");
                int quantity = userInput.nextInt();
                userInput.nextLine();

                if (menuItem != null) {
                    selectedRestaurant.addToMenu(menuItem, quantity);
                    System.out.println("Menu item added successfully with initial stock of " + quantity + ".");
                } else {
                    System.out.println("Invalid menu item type.");
                }
            } else {
                System.out.println("Invalid restaurant selection.");
            }
        } else {
            System.out.println("Invalid choice.");
        }
    }

    // Admin: 2. Delete Data
    private void deleteData() {
        System.out.print("Do you want to delete a Restaurant (R) or a Menu Item (M)? (R/M): ");
        String choice = userInput.nextLine().toUpperCase();

        if (choice.equals("R")) {
            Restaurant.displayRestaurants(restaurants);
            System.out.print("Enter restaurant index to delete: ");
            if (!userInput.hasNextInt()) {
                System.out.println("Invalid input. Please enter a number.");
                userInput.nextLine();
                return;
            }

            int restaurantIndex = userInput.nextInt() - 1;
            userInput.nextLine();

            if (restaurantIndex >= 0 && restaurantIndex < restaurants.size()) {
                removeData(restaurants.get(restaurantIndex));
            } else {
                System.out.println("Invalid restaurant selection.");
            }
        } else if (choice.equals("M")) {
            Restaurant.displayRestaurants(restaurants);
            System.out.print("Enter restaurant index to delete a menu item: ");
            int restaurantIndex = userInput.nextInt() - 1;
            userInput.nextLine();

            if (restaurantIndex >= 0 && restaurantIndex < restaurants.size()) {
                Restaurant selectedRestaurant = restaurants.get(restaurantIndex);
                System.out.print("Enter the name of the menu item to delete: ");
                String itemName = userInput.nextLine();
                selectedRestaurant.removeFromMenu(itemName);
                System.out.println("Menu item deleted successfully.");
            } else {
                System.out.println("Invalid restaurant selection.");
            }
        } else {
            System.out.println("Invalid choice.");
        }
    }

    // Admin 3. Modify Data
    private void modifyData() {
        System.out.print("Do you want to modify a Restaurant (R) or a Menu Item (M)? (R/M): ");
        String choice = userInput.nextLine().toUpperCase();
        System.out.println("-----------------------------------");

        if (choice.equals("R")) {
            Restaurant.displayRestaurants(restaurants);
            System.out.print("Enter the restaurant index to modify: ");
            int restaurantIndex = userInput.nextInt() - 1;
            userInput.nextLine();
            System.out.println("-----------------------------------");

            if (restaurantIndex >= 0 && restaurantIndex < restaurants.size()) {
                Restaurant oldRestaurant = restaurants.get(restaurantIndex);

                System.out.print("Enter new name for the restaurant: ");
                String newName = userInput.nextLine();

                Restaurant updatedRestaurant = new Restaurant(newName);

                updateData(oldRestaurant, updatedRestaurant);
            } else {
                System.out.println("Invalid restaurant selection.");
            }
        } else if (choice.equals("M")) {
            Restaurant.displayRestaurants(restaurants);
            System.out.print("Enter the restaurant index to modify its menu: ");
            int restaurantIndex = userInput.nextInt() - 1;
            userInput.nextLine();
            System.out.println("-----------------------------------");

            if (restaurantIndex >= 0 && restaurantIndex < restaurants.size()) {
                Restaurant selectedRestaurant = restaurants.get(restaurantIndex);
                MenuItem.displayMenuItems(selectedRestaurant);

                System.out.print("Enter the name of the menu item to modify: ");
                String itemName = userInput.nextLine();
                MenuItem menuItem = MenuItem.findMenuItem(selectedRestaurant, itemName);
                System.out.println("-----------------------------------");

                if (menuItem != null) {
                    System.out.println("Selected Item: " + menuItem.getItemName());
                    System.out.println("1. Change Name");
                    System.out.println("2. Change Price");
                    System.out.println("3. Change Description");
                    if (menuItem instanceof FoodItem) {
                        System.out.println("4. Change Cuisine Type");
                    } else if (menuItem instanceof DrinkItem) {
                        System.out.println("4. Change Beverage Type");
                    }
                    System.out.println("-----------------------------------");
                    System.out.print("Enter your choice: ");
                    int inputNum = userInput.nextInt();
                    userInput.nextLine();

                    switch (inputNum) {
                        case 1:
                            System.out.print("Enter new name: ");
                            menuItem.setItemName(userInput.nextLine());
                            System.out.println("-----------------------------------");
                            System.out.println("Menu item updated successfully.");
                            break;
                        case 2:
                            System.out.print("Enter new price: ");
                            menuItem.setPrice(userInput.nextDouble());
                            userInput.nextLine();
                            System.out.println("-----------------------------------");
                            System.out.println("Menu item updated successfully.");
                            break;
                        case 3:
                            System.out.print("Enter new description: ");
                            menuItem.setDescription(userInput.nextLine());
                            System.out.println("-----------------------------------");
                            System.out.println("Menu item updated successfully.");
                            break;
                        case 4:
                            if (menuItem instanceof FoodItem) {
                                System.out.print("Enter new cuisine type: ");
                                ((FoodItem) menuItem).setCuisineType(userInput.nextLine());
                                System.out.println("-----------------------------------");
                            } else if (menuItem instanceof DrinkItem) {
                                System.out.print("Enter new beverage type: ");
                                ((DrinkItem) menuItem).setBeverageType(userInput.nextLine());
                                System.out.println("-----------------------------------");
                                System.out.println("Menu item updated successfully.");
                            }
                            break;
                        default:
                            System.out.println("Invalid choice.");
                            break;
                    }
                } else {
                    System.out.println("Menu item not found.");
                }
            } else {
                System.out.println("Invalid restaurant selection.");
            }
        } else {
            System.out.println("Invalid choice.");
        }
    }

    // Admin 4. View Data
    private void viewData() {
        for (Restaurant restaurant : restaurants) {
            System.out.println("Restaurant: " + restaurant.getRestaurantName());
            List<MenuItem> menu = restaurant.getMenu();
            if (menu.isEmpty()) {
                System.out.println("  No menu items available.");
            } else {
                for (MenuItem item : menu) {
                    int stock = restaurant.getStock().getOrDefault(item, 0);
                    System.out.println("  " + item.getItemName() + " - $" + item.getPrice() + " - Stock: " + stock);
                }
            }
            System.out.println();
        }
    }

    // Admin 5. Restock Item
    private void restockItem() {
        Restaurant.displayRestaurants(restaurants);
        System.out.print("Enter restaurant's index to restock one of its menu item: ");
        int restaurantIndex = userInput.nextInt() - 1;
        userInput.nextLine();
        System.out.println("-----------------------------------");

        if (restaurantIndex >= 0 && restaurantIndex < restaurants.size()) {
            Restaurant selectedRestaurant = restaurants.get(restaurantIndex);
            MenuItem.displayMenuItems(selectedRestaurant);

            System.out.print("Enter the name of the menu item to restock: ");
            String itemName = userInput.nextLine();
            MenuItem menuItem = MenuItem.findMenuItem(selectedRestaurant, itemName);

            if (menuItem != null) {
                System.out.print("Enter the quantity to add to stock: ");
                int quantity = userInput.nextInt();
                userInput.nextLine();

                if (quantity > 0) {
                    selectedRestaurant.updateInventory(menuItem, quantity);
                    System.out.println("-----------------------------------");
                    System.out.println("Restocked " + quantity + " units of " + itemName + ".");
                } else {
                    System.out.println("-----------------------------------");
                    System.out.println("Invalid quantity. Please enter a positive number.");
                }
            } else {
                System.out.println("-----------------------------------");
                System.out.println("Menu item not found.");
            }
        } else {
            System.out.println("Invalid restaurant selection.");
        }
    }

    protected void showAdminMenu(){
        int adminOption;
        do {
            displayAdminMenu();
            adminOption = userInput.nextInt();
            userInput.nextLine();
            System.out.println("-----------------------------------");

            switch (adminOption) {
                case 1:
                    insertData();
                    break;
                case 2:
                    deleteData();
                    break;
                case 3:
                    modifyData();
                    break;
                case 4:
                    viewData();
                    break;
                case 5:
                    restockItem();
                    break;
                case 6:
                    System.out.println("Exiting admin mode...");
                    System.out.println("-----------------------------------");
                    break;
                default:
                    System.out.println("Invalid option. Please try again (1-4).");
                    break;
            }
        } while (adminOption != 6);
    }

    private void displayAdminMenu(){
        System.out.print("""
                -----------------------------------
                Admin Menu:
                1. Add Restaurant/Menu
                2. Delete Restaurant/Menu
                3. Modify Restaurant/Menu
                4. View All Existing Restaurant and Its Menu
                5. Restock Menu Item
                6. Exit Admin Mode
                -----------------------------------
                Enter your choice:""");
    }
}