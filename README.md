# Restaurant Management System POS

## Table of Contents
1. [Introduction](#introduction)
2. [Login Page](#login-page)
3. [Registration Page](#registration-page)
4. [Customer Dashboard](#customer-dashboard)
5. [Receipt](#receipt)
6. [Admin Dashboard - Report Page](#admin-dashboard---report-page)
7. [Admin Dashboard - Inventory Page](#admin-dashboard---inventory-page)
8. [Admin Dashboard - Orders Page](#admin-dashboard---orders-page)
9. [Conclusion](#conclusion)
10. [Instructions for Installation and Setup](#instructions-for-installation-and-setup)

## Introduction
Welcome to the Restaurant Management System POS. This manual provides a step-by-step guide on how to use the system, including login, registration, ordering food, viewing receipts, and managing the restaurant’s backend as an admin.

## Login Page
![Login Page](path/to/your/image1.jpg)

1. **Username:** Enter your username.
2. **Password:** Enter your password.
3. **Login Button:** Click to log in to the system.
4. **Forgot Password:** Click if you have forgotten your password.

## Registration Page
![Registration Page](path/to/your/image2.jpg)

1. **Username:** Enter a unique username.
2. **Email:** Enter your email address.
3. **Password:** Create a secure password.
4. **Confirm Password:** Re-enter your password for confirmation.
5. **Register Button:** Click to create your account.

## Customer Dashboard
![Customer Dashboard](path/to/your/image3.jpg)

1. **Menu Items:** Browse available food items.
2. **Order Button:** Click to add items to your order.
3. **Cart:** View and manage items in your cart.
4. **Checkout Button:** Click to proceed to payment and finalize your order.

## Receipt
![Receipt](path/to/your/image4.jpg)

1. **Order Details:** Review the details of your order, including items, quantities, and prices.
2. **Total Amount:** The total amount to be paid.
3. **Payment Method:** Select your payment method.
4. **Confirm Order:** Click to confirm and place your order.

## Admin Dashboard - Report Page
![Admin Dashboard - Report Page](path/to/your/image5.jpg)

1. **Daily Report:** View daily sales and statistics.
2. **Weekly Report:** View weekly sales and statistics.
3. **Monthly Report:** View monthly sales and statistics.
4. **Annual Report:** View annual sales and statistics.
5. **Best Seller:** View the best-selling menu items.
6. **All Menu Item Sales:** View sales data for all menu items.

## Admin Dashboard - Inventory Page
![Admin Dashboard - Inventory Page](path/to/your/image6.jpg)

1. **Create Menu Item:** Add new menu items to the inventory.
2. **Read Menu Items:** View details of all menu items.
3. **Update Menu Item:** Edit existing menu items.
4. **Delete Menu Item:** Remove menu items from the inventory.

## Admin Dashboard - Orders Page
![Admin Dashboard - Orders Page](path/to/your/image7.jpg)

1. **Pending Orders:** View all pending orders.
2. **Complete Order:** Mark orders as completed once they are fulfilled.
3. **Cancel Order:** Cancel orders if necessary.

## Conclusion
This user manual provides a comprehensive guide to using the Restaurant Management System POS. For any further assistance, please contact our support team. Enjoy managing your restaurant efficiently!

## Instructions for Installation and Setup

### 1. Download the Zip File
Download the ZIP file containing the project.

### 2. Install IntelliJ and XAMPP
- Download and install [IntelliJ IDEA](https://www.jetbrains.com/idea/).
- Download and install [XAMPP](https://www.apachefriends.org/index.html).

### 3. Import Database
- Start XAMPP and ensure that Apache and MySQL servers are running.
- Import the SQL database file named `restaurant_pos.sql` into your MySQL database using phpMyAdmin or MySQL command line.

### 4. Run the Application
- Open IntelliJ IDEA.
- Navigate to the following path: `\RestaurantManagementSystem\src\main\java\com\restaurantmanagementsystem\pos\MainApp.java`.
- Open `MainApp.java` and run the application.

### 5. Login as Admin/Customer in the System
The below are the username and password for the login credentials:

- **Admin Login:**
  - Username: `admin`
  - Password: `admin123`

- **Customer Login:**
  - Username: `bran`
  - Password: `bran123`

### Note
- Ensure that the database connection settings in the application code (`MainApp.java`) match your MySQL database configuration.
- Make sure all dependencies are resolved in your IntelliJ project.

---

Replace `path/to/your/imageX.jpg` with the actual paths to your images. This README file provides a comprehensive guide to using and setting up your Restaurant Management System POS.
