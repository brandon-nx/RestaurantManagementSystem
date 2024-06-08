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
![Picture1RMS](https://github.com/brandon-nx/RestaurantManagementSystem/assets/139194109/6cc17192-a894-45b4-9adc-65695afccef2)


## Registration Page
![Picture2RMS](https://github.com/brandon-nx/RestaurantManagementSystem/assets/139194109/ee4e87dd-6431-4092-a1f9-7524b2a74d07)


## Customer Dashboard
![Picture3RMS](https://github.com/brandon-nx/RestaurantManagementSystem/assets/139194109/1a8d4c6e-5fea-4805-a2c8-9dc062c0cf6d)


## Receipt
![Picture4RMS](https://github.com/brandon-nx/RestaurantManagementSystem/assets/139194109/b8b95192-bfe2-46a4-a641-bed553a191fa)


## Admin Dashboard - Report Page
![Picture5RMS](https://github.com/brandon-nx/RestaurantManagementSystem/assets/139194109/fceeb8c2-5e47-41f9-9bae-24992536b86a)


## Admin Dashboard - Inventory Page
![Picture6RMS](https://github.com/brandon-nx/RestaurantManagementSystem/assets/139194109/7123be86-b3e3-455c-84a7-b62b62588000)


## Admin Dashboard - Orders Page
![Picture7RMS](https://github.com/brandon-nx/RestaurantManagementSystem/assets/139194109/3a045f62-94e6-4e36-8041-6fb25b9ad9eb)


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
