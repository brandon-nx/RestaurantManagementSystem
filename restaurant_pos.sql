-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Apr 25, 2024 at 10:22 AM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.0.30

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `restaurant_pos`
--

-- --------------------------------------------------------

--
-- Table structure for table `menu_items`
--

CREATE TABLE `menu_items` (
  `product_id` varchar(10) NOT NULL,
  `name` varchar(255) NOT NULL,
  `price` decimal(10,2) NOT NULL,
  `image_path` varchar(255) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `category` varchar(255) DEFAULT NULL,
  `stock` int(11) DEFAULT 0,
  `status` enum('available','unavailable') DEFAULT 'available'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `menu_items`
--

INSERT INTO `menu_items` (`product_id`, `name`, `price`, `image_path`, `created_at`, `updated_at`, `category`, `stock`, `status`) VALUES
('P-001', 'Burger', 5.00, '/burger.jpg', '2024-04-07 03:52:54', '2024-04-25 04:33:56', 'Entrées', 95, 'available'),
('P-002', 'Chicken Chop', 12.50, '/chickenchop.jpg', '2024-04-07 16:29:25', '2024-04-25 07:08:38', 'Entrées', 97, 'available'),
('P-003', 'Fish and Chip', 10.00, '/fishandchip.jpg', '2024-04-07 16:29:25', '2024-04-25 03:44:04', 'Entrées', 98, 'unavailable'),
('P-004', 'Pasta', 8.00, '/pasta.jpg', '2024-04-07 16:29:25', '2024-04-25 04:06:07', 'Entrées', 0, 'available'),
('P-005', 'Pizza', 15.00, '/pizza.jpg', '2024-04-07 16:29:25', '2024-04-25 04:34:03', 'Entrées', 4, 'available'),
('P-006', 'Cola', 1.50, '/cola.jpg', '2024-04-08 15:46:38', '2024-04-25 04:05:41', 'Beverages', 0, 'available'),
('P-007', 'Cheesecake', 10.50, '/cheesecake.jpg', '2024-04-08 15:46:38', '2024-04-25 07:08:38', 'Desserts', 99, 'available'),
('P-008', 'Salad', 10.50, '/salad.jpg', '2024-04-08 15:46:38', '2024-04-25 03:44:10', 'Appetizers', 97, 'unavailable'),
('P-009', 'French Fries', 5.00, '/frenchfries.jpeg', '2024-04-08 15:46:38', '2024-04-24 06:01:18', 'Sides', 98, 'available'),
('P-010', 'Mushroom Soup', 5.50, 'file:/C:/Users/Hp/Downloads/mushroomsoup.jpg', '2024-04-12 08:45:15', '2024-04-25 04:31:47', 'Appetizers', 83, 'available'),
('P-011', 'Deviled Egg', 5.50, 'file:/C:/Users/Hp/Downloads/harissa-deviled-eggs2-1656449969.jpg', '2024-04-24 06:02:11', '2024-04-25 07:27:34', 'Appetizers', 69, 'available'),
('P-012', 'Brownies', 12.00, 'file:/C:/Users/Hp/Downloads/Vegan-Brownies.jpg', '2024-04-25 03:36:11', '2024-04-25 07:27:34', 'Desserts', 10, 'available'),
('P-013', 'Lamb Chop', 20.50, 'file:/C:/Users/Hp/Downloads/lambchop.jpeg', '2024-04-25 07:12:16', '2024-04-25 07:27:34', 'Entrées', 11, 'available');

-- --------------------------------------------------------

--
-- Table structure for table `orders`
--

CREATE TABLE `orders` (
  `order_id` varchar(10) NOT NULL,
  `user_id` varchar(10) NOT NULL,
  `total_amount` decimal(10,2) NOT NULL,
  `status` enum('pending','done','cancelled') NOT NULL DEFAULT 'pending',
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `orders`
--

INSERT INTO `orders` (`order_id`, `user_id`, `total_amount`, `status`, `created_at`, `updated_at`) VALUES
('O-001', 'U-001', 30.74, 'done', '2022-03-02 02:11:52', '2024-04-23 14:44:27'),
('O-002', 'U-001', 35.38, 'done', '2022-05-07 01:59:18', '2024-04-23 14:44:27'),
('O-003', 'U-001', 13.92, 'done', '2023-12-04 04:03:01', '2024-04-24 01:52:24'),
('O-004', 'U-005', 64.38, 'done', '2023-10-05 10:04:42', '2024-04-23 14:44:32'),
('O-005', 'U-005', 29.00, 'done', '2023-07-20 11:04:49', '2024-04-23 06:02:21'),
('O-006', 'U-005', 14.50, 'done', '2024-06-20 12:04:58', '2024-04-23 06:02:30'),
('O-007', 'U-006', 18.56, 'done', '2024-07-18 02:54:30', '2024-04-23 06:03:55'),
('O-008', 'U-006', 30.74, 'done', '2024-08-29 02:55:09', '2024-04-23 06:04:02'),
('O-009', 'U-003', 31.32, 'done', '2024-09-18 02:55:41', '2024-04-24 03:23:35'),
('O-010', 'U-001', 12.18, 'done', '2024-01-18 01:04:45', '2024-04-23 06:04:18'),
('O-011', 'U-001', 17.98, 'done', '2024-05-20 17:04:55', '2024-04-20 17:10:35'),
('O-012', 'U-003', 20.30, 'done', '2022-04-20 17:05:11', '2024-04-23 14:44:25'),
('O-013', 'U-003', 26.68, 'done', '2022-01-04 17:05:18', '2024-04-23 14:44:26'),
('O-014', 'U-007', 34.80, 'cancelled', '2023-03-20 17:05:51', '2024-04-24 01:16:19'),
('O-015', 'U-007', 23.78, 'done', '2023-05-20 17:05:57', '2024-04-24 01:52:32'),
('O-016', 'U-008', 72.50, 'done', '2024-04-23 05:56:05', '2024-04-23 05:57:55'),
('O-017', 'U-007', 30.16, 'done', '2024-04-23 05:58:39', '2024-04-24 05:49:31'),
('O-018', 'U-003', 59.16, 'done', '2024-04-23 05:58:55', '2024-04-23 14:44:26'),
('O-019', 'U-003', 29.00, 'done', '2024-04-23 05:59:05', '2024-04-23 14:44:26'),
('O-020', 'U-001', 38.28, 'done', '2024-04-23 05:59:24', '2024-04-23 05:59:52'),
('O-021', 'U-001', 12.18, 'done', '2024-04-23 12:42:39', '2024-04-23 14:44:30'),
('O-022', 'U-001', 11.60, 'done', '2024-04-23 12:47:17', '2024-04-23 14:44:30'),
('O-023', 'U-001', 11.60, 'cancelled', '2024-04-23 12:52:33', '2024-04-24 01:52:26'),
('O-024', 'U-001', 11.60, 'done', '2024-04-23 13:02:14', '2024-04-24 13:38:50'),
('O-025', 'U-001', 52.20, 'cancelled', '2024-04-23 13:10:51', '2024-04-24 01:16:21'),
('O-026', 'U-001', 9.28, 'done', '2024-04-23 13:12:16', '2024-04-23 14:44:30'),
('O-027', 'U-011', 18.56, 'done', '2024-04-23 14:43:54', '2024-04-24 13:38:20'),
('O-028', 'U-001', 18.56, 'done', '2024-04-24 05:26:18', '2024-04-24 06:02:51'),
('O-029', 'U-009', 11.60, 'done', '2024-04-24 05:35:53', '2024-04-24 13:38:59'),
('O-030', 'U-001', 88.74, 'done', '2024-04-24 05:49:08', '2024-04-24 06:02:52'),
('O-031', 'U-001', 43.50, 'done', '2024-04-24 06:01:18', '2024-04-24 06:02:54'),
('O-032', 'U-001', 6.38, 'done', '2024-04-24 06:02:27', '2024-04-24 06:02:55'),
('O-033', 'U-001', 12.76, 'done', '2024-04-24 13:37:43', '2024-04-25 07:16:06'),
('O-034', 'U-009', 41.18, 'pending', '2024-04-24 13:38:01', '2024-04-24 13:39:10'),
('O-035', 'U-001', 18.56, 'done', '2024-04-25 03:52:10', '2024-04-25 07:16:08'),
('O-036', 'U-001', 35.96, 'done', '2024-04-25 04:06:07', '2024-04-25 07:16:09'),
('O-037', 'U-008', 12.18, 'pending', '2024-04-25 04:10:29', '2024-04-25 04:10:29'),
('O-038', 'U-001', 12.76, 'pending', '2024-04-25 04:31:47', '2024-04-25 04:31:47'),
('O-039', 'U-009', 5.80, 'done', '2024-04-25 04:33:56', '2024-04-25 07:16:11'),
('O-040', 'U-009', 17.40, 'done', '2024-04-25 04:34:03', '2024-04-25 07:16:13'),
('O-041', 'U-001', 6.38, 'done', '2024-04-25 07:01:30', '2024-04-25 07:16:16'),
('O-042', 'U-001', 6.38, 'done', '2024-04-25 07:01:38', '2024-04-25 07:16:18'),
('O-043', 'U-001', 26.68, 'cancelled', '2024-04-25 07:08:38', '2024-04-25 07:28:36'),
('O-044', 'U-001', 13.92, 'pending', '2024-04-25 07:08:44', '2024-04-25 07:08:44'),
('O-045', 'U-001', 44.08, 'done', '2024-04-25 07:27:34', '2024-04-25 07:28:32');

-- --------------------------------------------------------

--
-- Table structure for table `order_items`
--

CREATE TABLE `order_items` (
  `order_item_id` varchar(10) NOT NULL,
  `order_id` varchar(10) NOT NULL,
  `product_id` varchar(10) NOT NULL,
  `quantity` int(11) NOT NULL,
  `price` decimal(10,2) NOT NULL,
  `product_name` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `order_items`
--

INSERT INTO `order_items` (`order_item_id`, `order_id`, `product_id`, `quantity`, `price`, `product_name`) VALUES
('OI-001', 'O-001', 'P-008', 1, 10.50, 'Salad'),
('OI-002', 'O-001', 'P-010', 1, 5.50, 'Mushroom Soup'),
('OI-003', 'O-001', 'P-007', 1, 10.50, 'Cheesecake'),
('OI-004', 'O-002', 'P-004', 1, 8.00, 'Pasta'),
('OI-005', 'O-002', 'P-003', 1, 10.00, 'Fish and Chip'),
('OI-006', 'O-002', 'P-002', 1, 12.50, 'Chicken Chop'),
('OI-007', 'O-003', 'P-010', 1, 5.50, 'Mushroom Soup'),
('OI-008', 'O-003', 'P-009', 1, 5.00, 'French Fries'),
('OI-009', 'O-003', 'P-006', 1, 1.50, 'Cola'),
('OI-010', 'O-004', 'P-009', 1, 5.00, 'French Fries'),
('OI-011', 'O-004', 'P-001', 1, 5.00, 'Burger'),
('OI-012', 'O-004', 'P-002', 1, 12.50, 'Chicken Chop'),
('OI-013', 'O-004', 'P-003', 1, 10.00, 'Fish and Chip'),
('OI-014', 'O-004', 'P-004', 1, 8.00, 'Pasta'),
('OI-015', 'O-004', 'P-005', 1, 15.00, 'Pizza'),
('OI-016', 'O-005', 'P-002', 2, 12.50, 'Chicken Chop'),
('OI-017', 'O-006', 'P-002', 1, 12.50, 'Chicken Chop'),
('OI-018', 'O-007', 'P-008', 1, 10.50, 'Salad'),
('OI-019', 'O-007', 'P-010', 1, 5.50, 'Mushroom Soup'),
('OI-020', 'O-008', 'P-001', 1, 5.00, 'Burger'),
('OI-021', 'O-008', 'P-005', 1, 15.00, 'Pizza'),
('OI-022', 'O-008', 'P-009', 1, 5.00, 'French Fries'),
('OI-023', 'O-008', 'P-006', 1, 1.50, 'Cola'),
('OI-024', 'O-009', 'P-010', 1, 5.50, 'Mushroom Soup'),
('OI-025', 'O-009', 'P-009', 1, 5.00, 'French Fries'),
('OI-026', 'O-009', 'P-001', 1, 5.00, 'Burger'),
('OI-027', 'O-009', 'P-003', 1, 10.00, 'Fish and Chip'),
('OI-028', 'O-009', 'P-006', 1, 1.50, 'Cola'),
('OI-029', 'O-010', 'P-008', 1, 10.50, 'Salad'),
('OI-030', 'O-011', 'P-007', 1, 10.50, 'Cheesecake'),
('OI-031', 'O-011', 'P-009', 1, 5.00, 'French Fries'),
('OI-032', 'O-012', 'P-001', 1, 5.00, 'Burger'),
('OI-033', 'O-012', 'P-002', 1, 12.50, 'Chicken Chop'),
('OI-034', 'O-013', 'P-005', 1, 15.00, 'Pizza'),
('OI-035', 'O-013', 'P-004', 1, 8.00, 'Pasta'),
('OI-036', 'O-014', 'P-009', 1, 5.00, 'French Fries'),
('OI-037', 'O-014', 'P-001', 5, 5.00, 'Burger'),
('OI-038', 'O-015', 'P-004', 1, 8.00, 'Pasta'),
('OI-039', 'O-015', 'P-002', 1, 12.50, 'Chicken Chop'),
('OI-040', 'O-016', 'P-008', 1, 10.50, 'Salad'),
('OI-041', 'O-016', 'P-010', 1, 5.50, 'Mushroom Soup'),
('OI-042', 'O-016', 'P-007', 4, 10.50, 'Cheesecake'),
('OI-043', 'O-016', 'P-006', 3, 1.50, 'Cola'),
('OI-044', 'O-017', 'P-008', 1, 10.50, 'Salad'),
('OI-045', 'O-017', 'P-009', 1, 5.00, 'French Fries'),
('OI-046', 'O-017', 'P-007', 1, 10.50, 'Cheesecake'),
('OI-047', 'O-018', 'P-008', 1, 10.50, 'Salad'),
('OI-048', 'O-018', 'P-009', 1, 5.00, 'French Fries'),
('OI-049', 'O-018', 'P-001', 1, 5.00, 'Burger'),
('OI-050', 'O-018', 'P-002', 1, 12.50, 'Chicken Chop'),
('OI-051', 'O-018', 'P-003', 1, 10.00, 'Fish and Chip'),
('OI-052', 'O-018', 'P-004', 1, 8.00, 'Pasta'),
('OI-053', 'O-019', 'P-009', 5, 5.00, 'French Fries'),
('OI-054', 'O-020', 'P-008', 1, 10.50, 'Salad'),
('OI-055', 'O-020', 'P-002', 1, 12.50, 'Chicken Chop'),
('OI-056', 'O-020', 'P-003', 1, 10.00, 'Fish and Chip'),
('OI-057', 'O-022', 'P-003', 1, 10.00, 'Fish and Chip'),
('OI-058', 'O-023', 'P-003', 1, 10.00, 'Fish and Chip'),
('OI-059', 'O-024', 'P-003', 1, 10.00, 'Fish and Chip'),
('OI-060', 'O-025', 'P-005', 3, 15.00, 'Pizza'),
('OI-061', 'O-026', 'P-004', 1, 8.00, 'Pasta'),
('OI-062', 'O-027', 'P-008', 1, 10.50, 'Salad'),
('OI-063', 'O-027', 'P-010', 1, 5.50, 'Mushroom Soup'),
('OI-064', 'O-028', 'P-008', 1, 10.50, 'Salad'),
('OI-065', 'O-028', 'P-010', 1, 5.50, 'Mushroom Soup'),
('OI-066', 'O-029', 'P-009', 1, 5.00, 'French Fries'),
('OI-067', 'O-029', 'P-001', 1, 5.00, 'Burger'),
('OI-068', 'O-030', 'P-008', 1, 10.50, 'Salad'),
('OI-069', 'O-030', 'P-010', 12, 5.50, 'Mushroom Soup'),
('OI-070', 'O-031', 'P-001', 1, 5.00, 'Burger'),
('OI-071', 'O-031', 'P-002', 1, 12.50, 'Chicken Chop'),
('OI-072', 'O-031', 'P-005', 1, 15.00, 'Pizza'),
('OI-073', 'O-031', 'P-009', 1, 5.00, 'French Fries'),
('OI-074', 'O-032', 'P-011', 1, 5.50, 'Deviled Egg'),
('OI-075', 'O-033', 'P-010', 1, 5.50, 'Mushroom Soup'),
('OI-076', 'O-033', 'P-011', 1, 5.50, 'Deviled Egg'),
('OI-077', 'O-034', 'P-001', 1, 5.00, 'Burger'),
('OI-078', 'O-034', 'P-002', 1, 12.50, 'Chicken Chop'),
('OI-079', 'O-034', 'P-003', 1, 10.00, 'Fish and Chip'),
('OI-080', 'O-034', 'P-004', 1, 8.00, 'Pasta'),
('OI-081', 'O-035', 'P-004', 2, 8.00, 'Pasta'),
('OI-082', 'O-036', 'P-004', 2, 8.00, 'Pasta'),
('OI-083', 'O-036', 'P-005', 1, 15.00, 'Pizza'),
('OI-084', 'O-037', 'P-010', 1, 5.50, 'Mushroom Soup'),
('OI-085', 'O-037', 'P-001', 1, 5.00, 'Burger'),
('OI-086', 'O-038', 'P-010', 1, 5.50, 'Mushroom Soup'),
('OI-087', 'O-038', 'P-011', 1, 5.50, 'Deviled Egg'),
('OI-088', 'O-039', 'P-001', 1, 5.00, 'Burger'),
('OI-089', 'O-040', 'P-005', 1, 15.00, 'Pizza'),
('OI-090', 'O-041', 'P-011', 1, 5.50, 'Deviled Egg'),
('OI-091', 'O-042', 'P-011', 1, 5.50, 'Deviled Egg'),
('OI-092', 'O-043', 'P-002', 1, 12.50, 'Chicken Chop'),
('OI-093', 'O-043', 'P-007', 1, 10.50, 'Cheesecake'),
('OI-094', 'O-044', 'P-012', 1, 12.00, 'Brownies'),
('OI-095', 'O-045', 'P-011', 1, 5.50, 'Deviled Egg'),
('OI-096', 'O-045', 'P-013', 1, 20.50, 'Lamb Chop'),
('OI-097', 'O-045', 'P-012', 1, 12.00, 'Brownies');

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `user_id` varchar(10) NOT NULL,
  `username` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `role` enum('customer','admin') NOT NULL,
  `contact` varchar(50) NOT NULL,
  `address` text NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`user_id`, `username`, `password`, `role`, `contact`, `address`, `created_at`) VALUES
('U-001', 'bran', 'bran123', 'customer', '0123456789', 'qwertyui', '2024-04-05 06:34:59'),
('U-002', 'admin', 'admin123', 'admin', '1234567890', 'qwertyuiop', '2024-04-05 06:42:36'),
('U-003', 'ali', 'ali123', 'customer', '0123456789', 'qwe,12345qwert', '2024-04-17 03:39:36'),
('U-004', 'corn', 'corn123', 'customer', '0123456789', 'qwert,12345qwert', '2024-04-17 07:53:01'),
('U-005', 'cat', 'cat123', 'customer', '1234567890', 'qwertyui', '2024-04-18 18:04:18'),
('U-006', 'mandy', 'mandy123', 'customer', '0123456789', 'qwertyui,1234567', '2024-04-19 02:52:42'),
('U-007', 'mina', 'mina123', 'customer', '0123456789', 'qwertyuio', '2024-04-20 17:05:38'),
('U-008', 'gigi', 'gigi123', 'customer', '1234567890', 'qwertyuiop', '2024-04-23 05:55:48'),
('U-009', 'yoyo', 'yoyo123', 'customer', '1234567890', 'qwer', '2024-04-23 11:13:36'),
('U-010', 'henry', 'henry123', 'customer', '0473946574', 'qwertyuiop[', '2024-04-23 11:27:12'),
('U-011', 'hihi', 'hihi123', 'customer', '1234567890', 'qwertyuiop', '2024-04-23 14:43:19');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `menu_items`
--
ALTER TABLE `menu_items`
  ADD PRIMARY KEY (`product_id`);

--
-- Indexes for table `orders`
--
ALTER TABLE `orders`
  ADD PRIMARY KEY (`order_id`),
  ADD KEY `user_id` (`user_id`);

--
-- Indexes for table `order_items`
--
ALTER TABLE `order_items`
  ADD PRIMARY KEY (`order_item_id`),
  ADD KEY `order_id` (`order_id`),
  ADD KEY `product_id` (`product_id`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`user_id`),
  ADD UNIQUE KEY `username` (`username`);

--
-- Constraints for dumped tables
--

--
-- Constraints for table `orders`
--
ALTER TABLE `orders`
  ADD CONSTRAINT `orders_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`);

--
-- Constraints for table `order_items`
--
ALTER TABLE `order_items`
  ADD CONSTRAINT `order_items_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `orders` (`order_id`),
  ADD CONSTRAINT `order_items_ibfk_2` FOREIGN KEY (`product_id`) REFERENCES `menu_items` (`product_id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
