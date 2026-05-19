CREATE DATABASE  IF NOT EXISTS `restaurant_website` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `restaurant_website`;
-- MySQL dump 10.13  Distrib 8.0.45, for macos15 (arm64)
--
-- Host: localhost    Database: restaurant_website
-- ------------------------------------------------------
-- Server version	8.0.45

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `areas`
--

DROP TABLE IF EXISTS `areas`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `areas` (
  `area_id` smallint unsigned NOT NULL AUTO_INCREMENT,
  `restaurant_id` smallint unsigned NOT NULL,
  `area_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` text COLLATE utf8mb4_unicode_ci,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`area_id`),
  KEY `fk_areas_restaurant` (`restaurant_id`),
  CONSTRAINT `fk_areas_restaurant` FOREIGN KEY (`restaurant_id`) REFERENCES `restaurants` (`restaurant_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `areas`
--

LOCK TABLES `areas` WRITE;
/*!40000 ALTER TABLE `areas` DISABLE KEYS */;
INSERT INTO `areas` VALUES (1,1,'Khu vực tầng 1','','2026-04-20 00:53:30','2026-04-20 00:53:30');
/*!40000 ALTER TABLE `areas` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `blogs`
--

DROP TABLE IF EXISTS `blogs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `blogs` (
  `blog_id` int unsigned NOT NULL AUTO_INCREMENT,
  `user_id` bigint unsigned NOT NULL,
  `title` varchar(255) NOT NULL,
  `content` text NOT NULL,
  `create_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `status` tinyint DEFAULT '1',
  PRIMARY KEY (`blog_id`),
  KEY `fk_blog_user` (`user_id`),
  CONSTRAINT `fk_blog_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `blogs`
--

LOCK TABLES `blogs` WRITE;
/*!40000 ALTER TABLE `blogs` DISABLE KEYS */;
/*!40000 ALTER TABLE `blogs` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cart_items`
--

DROP TABLE IF EXISTS `cart_items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cart_items` (
  `cart_item_id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `user_id` bigint unsigned NOT NULL,
  `restaurant_product_id` int unsigned NOT NULL,
  `quantity` smallint unsigned NOT NULL DEFAULT '1',
  `added_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`cart_item_id`)
) ENGINE=InnoDB AUTO_INCREMENT=77 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cart_items`
--

LOCK TABLES `cart_items` WRITE;
/*!40000 ALTER TABLE `cart_items` DISABLE KEYS */;
/*!40000 ALTER TABLE `cart_items` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `categories`
--

DROP TABLE IF EXISTS `categories`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `categories` (
  `category_id` smallint unsigned NOT NULL AUTO_INCREMENT,
  `category_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` text COLLATE utf8mb4_unicode_ci,
  `parent_id` smallint unsigned DEFAULT NULL,
  PRIMARY KEY (`category_id`),
  KEY `fk_category_parent` (`parent_id`),
  CONSTRAINT `fk_category_parent` FOREIGN KEY (`parent_id`) REFERENCES `categories` (`category_id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `categories`
--

LOCK TABLES `categories` WRITE;
/*!40000 ALTER TABLE `categories` DISABLE KEYS */;
INSERT INTO `categories` VALUES (1,'Thực đơn','Món ăn được nấu tại quán',NULL),(2,'Mua sắm','Thực phẩm khô đóng gói',NULL),(3,'Món chính',NULL,1),(4,'Đồ uống',NULL,1),(5,'Món khai vị',NULL,1),(6,'Sữa',NULL,2),(8,'Bia',NULL,2),(9,'Nước',NULL,2),(10,'Thực phẩm đông lạnh','',2),(11,'Bánh kẹo',NULL,2),(12,'Thực phẩm khác',NULL,2);
/*!40000 ALTER TABLE `categories` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `comments`
--

DROP TABLE IF EXISTS `comments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `comments` (
  `comment_id` int unsigned NOT NULL AUTO_INCREMENT,
  `blog_id` int unsigned NOT NULL,
  `user_id` bigint unsigned NOT NULL,
  `content` text NOT NULL,
  `create_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `parent_comment_id` int unsigned DEFAULT NULL,
  PRIMARY KEY (`comment_id`),
  KEY `fk_comment_blog` (`blog_id`),
  KEY `fk_comment_user` (`user_id`),
  KEY `fk_parent_comment` (`parent_comment_id`),
  CONSTRAINT `fk_comment_blog` FOREIGN KEY (`blog_id`) REFERENCES `blogs` (`blog_id`) ON DELETE CASCADE,
  CONSTRAINT `fk_comment_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE,
  CONSTRAINT `fk_parent_comment` FOREIGN KEY (`parent_comment_id`) REFERENCES `comments` (`comment_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `comments`
--

LOCK TABLES `comments` WRITE;
/*!40000 ALTER TABLE `comments` DISABLE KEYS */;
/*!40000 ALTER TABLE `comments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `discounts`
--

DROP TABLE IF EXISTS `discounts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `discounts` (
  `discount_id` int unsigned NOT NULL AUTO_INCREMENT,
  `discount_name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `discount_type` enum('PERCENTAGE','FIXED_AMOUNT') COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'Loại: Phần trăm hoặc Số tiền cố định',
  `discount_value` decimal(12,2) NOT NULL COMMENT 'Giá trị giảm',
  `start_date` datetime NOT NULL,
  `end_date` datetime NOT NULL,
  `status` tinyint DEFAULT '1' COMMENT '1: Hoạt động, 0: Tạm ngưng',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `restaurant_id` smallint unsigned NOT NULL,
  PRIMARY KEY (`discount_id`),
  KEY `fk_discounts_restaurant` (`restaurant_id`),
  CONSTRAINT `fk_discounts_restaurant` FOREIGN KEY (`restaurant_id`) REFERENCES `restaurants` (`restaurant_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `discounts`
--

LOCK TABLES `discounts` WRITE;
/*!40000 ALTER TABLE `discounts` DISABLE KEYS */;
INSERT INTO `discounts` VALUES (1,'Giảm giá tháng 5','PERCENTAGE',20.00,'2026-03-31 17:00:00','2026-05-29 17:00:00',1,'2026-03-30 07:09:01',1),(2,'Giảm giá phở','FIXED_AMOUNT',5000.00,'2026-04-23 17:00:00','2026-05-29 17:00:00',1,'2026-04-25 12:39:16',2);
/*!40000 ALTER TABLE `discounts` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `favourites`
--

DROP TABLE IF EXISTS `favourites`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `favourites` (
  `favourite_id` int unsigned NOT NULL AUTO_INCREMENT,
  `user_id` bigint unsigned NOT NULL,
  `restaurant_product_id` int unsigned NOT NULL,
  PRIMARY KEY (`favourite_id`),
  UNIQUE KEY `unique_user_product` (`user_id`,`restaurant_product_id`),
  KEY `fk_favourite_product` (`restaurant_product_id`),
  CONSTRAINT `fk_favourite_product` FOREIGN KEY (`restaurant_product_id`) REFERENCES `restaurant_products` (`restaurant_product_id`) ON DELETE CASCADE,
  CONSTRAINT `fk_favourite_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `favourites`
--

LOCK TABLES `favourites` WRITE;
/*!40000 ALTER TABLE `favourites` DISABLE KEYS */;
/*!40000 ALTER TABLE `favourites` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `notifications`
--

DROP TABLE IF EXISTS `notifications`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `notifications` (
  `notification_id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `user_id` bigint unsigned NOT NULL,
  `title` varchar(255) NOT NULL,
  `content` varchar(255) DEFAULT NULL,
  `is_read` tinyint(1) DEFAULT '0',
  `status` varchar(255) DEFAULT NULL,
  `link_url` varchar(255) DEFAULT NULL,
  `create_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`notification_id`),
  KEY `fk_noti_user` (`user_id`),
  CONSTRAINT `fk_noti_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `notifications`
--

LOCK TABLES `notifications` WRITE;
/*!40000 ALTER TABLE `notifications` DISABLE KEYS */;
INSERT INTO `notifications` VALUES (2,5,'Chào mừng bạn!','Tài khoản của bạn đã được khởi tạo thành công với 50 điểm thưởng.',1,'active',NULL,'2026-03-27 07:11:45'),(3,9,'Chào mừng bạn!','Tài khoản của bạn đã được khởi tạo thành công với 50 điểm thưởng.',1,'active',NULL,'2026-04-10 03:26:18'),(5,9,'⏰ Đừng quên ưu đãi của bạn!','Bạn vẫn còn mã \'MAGIAMGIA20K\' chưa sử dụng trong kho quà đấy!',1,'active','/my-vouchers','2026-04-16 01:47:44'),(6,11,'Chào mừng bạn!','Tài khoản của bạn đã được khởi tạo thành công với 50 điểm thưởng.',0,'active',NULL,'2026-05-13 03:02:32'),(7,12,'Chào mừng bạn!','Tài khoản của bạn đã được khởi tạo thành công với 50 điểm thưởng.',0,'active',NULL,'2026-05-17 21:04:37'),(8,13,'Chào mừng bạn!','Tài khoản của bạn đã được khởi tạo thành công với 50 điểm thưởng.',0,'active',NULL,'2026-05-18 01:07:15');
/*!40000 ALTER TABLE `notifications` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `order_items`
--

DROP TABLE IF EXISTS `order_items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `order_items` (
  `order_item_id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `order_id` bigint unsigned NOT NULL,
  `restaurant_product_id` int unsigned NOT NULL,
  `quantity` smallint unsigned NOT NULL DEFAULT '1',
  `item_name` varchar(255) NOT NULL,
  `price_at_purchase` decimal(19,2) NOT NULL,
  PRIMARY KEY (`order_item_id`),
  KEY `fk_items_order` (`order_id`),
  CONSTRAINT `fk_items_order` FOREIGN KEY (`order_id`) REFERENCES `orders` (`order_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order_items`
--

LOCK TABLES `order_items` WRITE;
/*!40000 ALTER TABLE `order_items` DISABLE KEYS */;
INSERT INTO `order_items` VALUES (6,6,2,1,'Đặc sản lòng lợn',30000.00),(7,7,1,1,'Phở bò',30000.00),(8,8,1,1,'Bún bò',30000.00),(9,9,2,1,'Phở bò',30000.00),(10,10,1,1,'Bún bò',24000.00),(11,11,1,1,'Bún bò',24000.00);
/*!40000 ALTER TABLE `order_items` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `orders`
--

DROP TABLE IF EXISTS `orders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `orders` (
  `order_id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `user_id` bigint unsigned NOT NULL,
  `voucher_id` int unsigned DEFAULT NULL,
  `table_id` int unsigned DEFAULT NULL,
  `restaurant_id` smallint unsigned NOT NULL,
  `total_price` decimal(19,2) NOT NULL DEFAULT '0.00',
  `amount_discounted` decimal(19,2) DEFAULT '0.00',
  `tax_amount` decimal(19,2) DEFAULT '0.00',
  `delivery_address` text COLLATE utf8mb4_unicode_ci,
  `status` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'Pending, Confirmed, Shipping, Completed, Cancelled',
  `order_type` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'Eat-in, Take-away, Delivery',
  `points_redeemed` int DEFAULT '0',
  `point_earned` int DEFAULT '0',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `shipping_fee` decimal(19,2) DEFAULT '0.00',
  PRIMARY KEY (`order_id`),
  KEY `fk_orders_user` (`user_id`),
  KEY `fk_orders_voucher` (`voucher_id`),
  KEY `fk_orders_table` (`table_id`),
  KEY `fk_orders_restaurant` (`restaurant_id`),
  CONSTRAINT `fk_orders_restaurant` FOREIGN KEY (`restaurant_id`) REFERENCES `restaurants` (`restaurant_id`),
  CONSTRAINT `fk_orders_table` FOREIGN KEY (`table_id`) REFERENCES `tables` (`table_id`),
  CONSTRAINT `fk_orders_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`),
  CONSTRAINT `fk_orders_voucher` FOREIGN KEY (`voucher_id`) REFERENCES `vouchers` (`voucher_id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `orders`
--

LOCK TABLES `orders` WRITE;
/*!40000 ALTER TABLE `orders` DISABLE KEYS */;
INSERT INTO `orders` VALUES (6,9,NULL,NULL,1,31500.00,0.00,1500.00,'Nhận tại cửa hàng','PAID','TAKEAWAY',NULL,31,'2026-05-03 23:32:50','2026-05-04 06:55:24',0.00),(7,9,NULL,NULL,1,31500.00,0.00,1500.00,'Nhận tại cửa hàng','CANCELLED','TAKEAWAY',NULL,31,'2026-05-04 00:00:27','2026-05-04 00:00:33',0.00),(8,5,NULL,NULL,1,31500.00,0.00,1500.00,'Nhận tại cửa hàng','PAID','TAKEAWAY',NULL,31,'2026-05-04 01:07:00','2026-05-04 01:07:29',0.00),(9,9,NULL,NULL,1,0.00,36000.00,6000.00,'Nhận tại cửa hàng','PAID','TAKEAWAY',36,0,'2026-05-10 20:01:06','2026-05-11 03:08:42',0.00),(10,9,NULL,NULL,1,28800.00,0.00,4800.00,'Nhận tại cửa hàng','PAID','TAKEAWAY',0,28,'2026-05-11 01:01:32','2026-05-11 01:02:10',0.00),(11,5,NULL,NULL,1,28800.00,0.00,4800.00,'Nhận tại cửa hàng','CANCELLED','TAKEAWAY',0,28,'2026-05-18 01:12:26','2026-05-18 01:12:39',0.00);
/*!40000 ALTER TABLE `orders` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `payment`
--

DROP TABLE IF EXISTS `payment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `payment` (
  `payment_id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `order_id` bigint unsigned NOT NULL,
  `restaurant_id` smallint unsigned NOT NULL,
  `payment_method` varchar(50) NOT NULL,
  `payment_status` varchar(50) NOT NULL,
  `paid_at` datetime DEFAULT NULL,
  PRIMARY KEY (`payment_id`),
  KEY `fk_payment_orders` (`order_id`),
  KEY `fk_payment_restaurant` (`restaurant_id`),
  CONSTRAINT `fk_payment_orders` FOREIGN KEY (`order_id`) REFERENCES `orders` (`order_id`) ON DELETE CASCADE,
  CONSTRAINT `fk_payment_restaurant` FOREIGN KEY (`restaurant_id`) REFERENCES `restaurants` (`restaurant_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `payment`
--

LOCK TABLES `payment` WRITE;
/*!40000 ALTER TABLE `payment` DISABLE KEYS */;
INSERT INTO `payment` VALUES (1,9,1,'TRANSFER','PENDING',NULL),(2,10,1,'TRANSFER','COMPLETED','2026-05-11 08:02:10'),(3,11,1,'TRANSFER','FAILED',NULL);
/*!40000 ALTER TABLE `payment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `point`
--

DROP TABLE IF EXISTS `point`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `point` (
  `point_id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `user_id` bigint unsigned NOT NULL,
  `order_id` bigint unsigned DEFAULT NULL,
  `amount` int NOT NULL,
  `type` varchar(255) DEFAULT NULL,
  `source_type` varchar(255) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`point_id`),
  KEY `fk_point_user` (`user_id`),
  CONSTRAINT `fk_point_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `point`
--

LOCK TABLES `point` WRITE;
/*!40000 ALTER TABLE `point` DISABLE KEYS */;
INSERT INTO `point` VALUES (2,5,NULL,50,'earn','register','2026-03-27 07:11:45'),(3,9,NULL,50,'earn','register','2026-04-10 03:26:18'),(4,11,NULL,50,'earn','register','2026-05-13 03:02:32'),(5,12,NULL,50,'earn','register','2026-05-17 21:04:37'),(6,13,NULL,50,'earn','register','2026-05-18 01:07:15');
/*!40000 ALTER TABLE `point` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `product_discounts`
--

DROP TABLE IF EXISTS `product_discounts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `product_discounts` (
  `product_discounts_id` int unsigned NOT NULL AUTO_INCREMENT,
  `restaurant_product_id` int unsigned NOT NULL,
  `discount_id` int unsigned NOT NULL,
  `special_price` decimal(12,2) DEFAULT NULL COMMENT 'Giá sau khi đã áp dụng giảm giá',
  PRIMARY KEY (`product_discounts_id`),
  KEY `fk_pd_restaurant_product` (`restaurant_product_id`),
  KEY `fk_pd_discount` (`discount_id`),
  CONSTRAINT `fk_pd_discount` FOREIGN KEY (`discount_id`) REFERENCES `discounts` (`discount_id`) ON DELETE CASCADE,
  CONSTRAINT `fk_pd_restaurant_product` FOREIGN KEY (`restaurant_product_id`) REFERENCES `restaurant_products` (`restaurant_product_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=32 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product_discounts`
--

LOCK TABLES `product_discounts` WRITE;
/*!40000 ALTER TABLE `product_discounts` DISABLE KEYS */;
INSERT INTO `product_discounts` VALUES (20,6,2,30000.00);
/*!40000 ALTER TABLE `product_discounts` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `products`
--

DROP TABLE IF EXISTS `products`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `products` (
  `product_id` int unsigned NOT NULL AUTO_INCREMENT,
  `category_id` smallint unsigned DEFAULT NULL,
  `name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `type` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `unit` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `preparation_time` smallint unsigned DEFAULT NULL COMMENT 'Thời gian chuẩn bị tính bằng phút',
  `image_url` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `description` text COLLATE utf8mb4_unicode_ci,
  PRIMARY KEY (`product_id`),
  KEY `fk_product_category` (`category_id`),
  CONSTRAINT `fk_product_category` FOREIGN KEY (`category_id`) REFERENCES `categories` (`category_id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `products`
--

LOCK TABLES `products` WRITE;
/*!40000 ALTER TABLE `products` DISABLE KEYS */;
INSERT INTO `products` VALUES (10,3,'Bún bò',NULL,'Bát',10,'/uploads/avatars/4a6e181d-0323-46e3-a6a2-5cd6e2733cbb.jpg','món này ngon '),(11,3,'Phở bò',NULL,'Đĩa',5,'/uploads/avatars/8c21f7c0-3fee-4804-af20-8943f3a75b2b.jpg','Phở bò ngon'),(14,6,'Sữa TH ít đường 180ml','Thực phẩm uống','Dây',NULL,'/uploads/avatars/ff3e701c-8eaa-4647-bf9f-0d7ff3b0bcd1.jpg','Sữa tuơi TH date mới ngày 1/4'),(15,3,'Phở bò tái lăn',NULL,'Bát',10,'/uploads/avatars/51338228-478b-4f8e-8efa-29abecadad70.jpg','Món phở bò tái kèm nước xương hầm đậm đà'),(16,11,'Bánh Chocopie','Thực phẩm đóng gói','Hộp',NULL,'/uploads/avatars/56763f38-637a-4d46-abeb-e90cb2133049.jpg','Bánh sô cô la '),(17,3,'Bánh mì hà nội',NULL,'Chiếc',5,'/uploads/avatars/e9b53d62-79ef-420d-8a39-5cbdf68aaaa8.jpg','Bánh mì pate');
/*!40000 ALTER TABLE `products` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ranked`
--

DROP TABLE IF EXISTS `ranked`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ranked` (
  `rank_id` int NOT NULL AUTO_INCREMENT,
  `rank_name` varchar(255) NOT NULL,
  `min_points` int unsigned NOT NULL DEFAULT '0',
  `discount_percent` double DEFAULT NULL,
  PRIMARY KEY (`rank_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ranked`
--

LOCK TABLES `ranked` WRITE;
/*!40000 ALTER TABLE `ranked` DISABLE KEYS */;
INSERT INTO `ranked` VALUES (1,'BRONZE',0,0),(2,'SILVER',500,5),(3,'GOLD',1000,10),(4,'DIAMOND',2000,15),(5,'VIP',4000,20);
/*!40000 ALTER TABLE `ranked` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `reservation`
--

DROP TABLE IF EXISTS `reservation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `reservation` (
  `reservation_id` int unsigned NOT NULL AUTO_INCREMENT,
  `user_id` bigint unsigned NOT NULL,
  `restaurant_id` smallint unsigned NOT NULL,
  `reservation_time` datetime NOT NULL,
  `number_of_people` int NOT NULL,
  `status` varchar(50) DEFAULT NULL,
  `description` text,
  PRIMARY KEY (`reservation_id`),
  KEY `fk_user` (`user_id`),
  KEY `fk_restaurant` (`restaurant_id`),
  CONSTRAINT `fk_restaurant` FOREIGN KEY (`restaurant_id`) REFERENCES `restaurants` (`restaurant_id`),
  CONSTRAINT `fk_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `reservation`
--

LOCK TABLES `reservation` WRITE;
/*!40000 ALTER TABLE `reservation` DISABLE KEYS */;
INSERT INTO `reservation` VALUES (1,5,1,'2026-04-05 03:30:00',20,'CONFIRMED','không'),(3,7,1,'2026-04-10 08:30:00',6,'REJECTED','Nguyễn Đình Thắng - 0988822102 - Đặt tiệc sinh nhật - 1tr5'),(4,7,2,'2026-04-10 08:41:00',10,'PENDING','Nguyen Dinh Thiep - đặt tiệc'),(5,9,1,'2026-05-20 03:30:00',5,'PENDING','Không');
/*!40000 ALTER TABLE `reservation` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `restaurant_products`
--

DROP TABLE IF EXISTS `restaurant_products`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `restaurant_products` (
  `restaurant_product_id` int unsigned NOT NULL AUTO_INCREMENT,
  `product_id` int unsigned NOT NULL,
  `price` decimal(12,2) NOT NULL,
  `is_available` tinyint(1) DEFAULT '1',
  `stock_quantity` int DEFAULT '0',
  `is_featured` tinyint(1) DEFAULT '0',
  `restaurant_id` smallint unsigned NOT NULL,
  PRIMARY KEY (`restaurant_product_id`),
  KEY `fk_rp_product` (`product_id`),
  KEY `fk_restaurant_product_restaurant` (`restaurant_id`),
  CONSTRAINT `fk_restaurant_product_restaurant` FOREIGN KEY (`restaurant_id`) REFERENCES `restaurants` (`restaurant_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_rp_product` FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `restaurant_products`
--

LOCK TABLES `restaurant_products` WRITE;
/*!40000 ALTER TABLE `restaurant_products` DISABLE KEYS */;
INSERT INTO `restaurant_products` VALUES (1,10,30000.00,1,0,0,1),(2,11,30000.00,1,19,1,1),(5,14,24000.00,1,0,1,1),(6,15,35000.00,1,0,1,2),(7,16,23000.00,1,10,0,1),(8,17,15000.00,1,0,0,1);
/*!40000 ALTER TABLE `restaurant_products` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `restaurants`
--

DROP TABLE IF EXISTS `restaurants`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `restaurants` (
  `restaurant_id` smallint unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `slogan` varchar(255) DEFAULT NULL,
  `restaurant_avatar` varchar(255) DEFAULT NULL,
  `restaurant_img` varchar(255) DEFAULT NULL,
  `address` text NOT NULL,
  `phone` varchar(15) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `opening_time` time DEFAULT NULL,
  `closing_time` time DEFAULT NULL,
  `capacity` int unsigned DEFAULT NULL,
  `description` text,
  `tax_rate` decimal(5,2) DEFAULT '0.00',
  `default_shipping_fee` decimal(19,2) DEFAULT '0.00',
  `point_exchange_rate` int DEFAULT '1000',
  `point_redemption_rate` decimal(19,2) DEFAULT '0.00',
  `map_url` text,
  `status` tinyint unsigned DEFAULT '1',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`restaurant_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `restaurants`
--

LOCK TABLES `restaurants` WRITE;
/*!40000 ALTER TABLE `restaurants` DISABLE KEYS */;
INSERT INTO `restaurants` VALUES (1,'Pho Ham','TRUYỀN THỐNG & TẬN TÂM','/uploads/avatars/fa5087ff-1f97-42fa-bb86-6c7e8fedf853.jpg','/uploads/avatars/11bb5fda-4511-479a-9f4c-57fef2b87c8c.jpg','32 Hope Street','0899073387','huongvietliverpool@gmail.com','12:00:00','22:30:00',200,'Nơi gìn giữ tinh túy nước dùng từ xương ống hầm 24h, mang đến hương vị Phở Việt nguyên bản.',0.20,20000.00,100000,1000.00,'<iframe src=\"https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d19590.496148166334!2d-2.9654296468128774!3d53.40649766423945!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x487b21005fca2c8f%3A0x7ccd8f99a66ba194!2sPhoHam!5e1!3m2!1sen!2s!4v1775231069244!5m2!1sen!2s\" width=\"600\" height=\"450\" style=\"border:0;\" allowfullscreen=\"\" loading=\"lazy\" referrerpolicy=\"no-referrer-when-downgrade\"></iframe>',1,'2026-03-23 15:24:55'),(2,'Thượng Hải Restaurant','Hương vị đậm chất quê','','/uploads/avatars/bf661fe1-248e-4149-936f-0ef8f5a7835d.jpg','37 Bạch Liêu, thành phố Vinh, tỉnh Nghệ An','0988822102','songque@gmail.com','08:00:00','23:00:00',300,'Phục vụ các món ăn quê ngon ',0.08,12000.00,2000,400.00,'',1,'2026-04-03 09:12:09');
/*!40000 ALTER TABLE `restaurants` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `review_likes`
--

DROP TABLE IF EXISTS `review_likes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `review_likes` (
  `like_id` bigint NOT NULL AUTO_INCREMENT,
  `review_id` int unsigned NOT NULL,
  `user_id` bigint unsigned NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`like_id`),
  UNIQUE KEY `unique_user_review` (`user_id`,`review_id`),
  KEY `fk_like_review` (`review_id`),
  CONSTRAINT `fk_like_review` FOREIGN KEY (`review_id`) REFERENCES `reviews` (`review_id`) ON DELETE CASCADE,
  CONSTRAINT `fk_like_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `review_likes`
--

LOCK TABLES `review_likes` WRITE;
/*!40000 ALTER TABLE `review_likes` DISABLE KEYS */;
INSERT INTO `review_likes` VALUES (18,1,9,'2026-05-15 19:44:03'),(19,1,5,'2026-05-17 21:14:51'),(20,1,12,'2026-05-17 22:36:42');
/*!40000 ALTER TABLE `review_likes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `reviews`
--

DROP TABLE IF EXISTS `reviews`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `reviews` (
  `review_id` int unsigned NOT NULL AUTO_INCREMENT,
  `order_item_id` bigint unsigned DEFAULT NULL,
  `user_id` bigint unsigned NOT NULL,
  `comment` text COLLATE utf8mb4_unicode_ci,
  `rating` tinyint unsigned NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `status` enum('PENDING','PUBLISHED','HIDDEN') COLLATE utf8mb4_unicode_ci DEFAULT 'PUBLISHED',
  `parent_id` int unsigned DEFAULT NULL,
  PRIMARY KEY (`review_id`),
  KEY `fk_review_user` (`user_id`),
  KEY `fk_review_parent` (`parent_id`),
  KEY `fk_review_order_item` (`order_item_id`),
  KEY `idx_reviews_perf` (`status`,`parent_id`,`created_at` DESC),
  CONSTRAINT `fk_review_order_item` FOREIGN KEY (`order_item_id`) REFERENCES `order_items` (`order_item_id`) ON DELETE CASCADE,
  CONSTRAINT `fk_review_parent` FOREIGN KEY (`parent_id`) REFERENCES `reviews` (`review_id`) ON DELETE CASCADE,
  CONSTRAINT `fk_review_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE,
  CONSTRAINT `reviews_chk_1` CHECK ((`rating` between 1 and 5))
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `reviews`
--

LOCK TABLES `reviews` WRITE;
/*!40000 ALTER TABLE `reviews` DISABLE KEYS */;
INSERT INTO `reviews` VALUES (1,6,9,'Nhân viên thân thiện, món ăn ngon',4,'2026-05-06 02:03:05','PUBLISHED',NULL),(5,NULL,5,'cảm ơn bạn',5,'2026-05-18 00:06:14','PUBLISHED',1),(7,NULL,7,'Cảm ơn bạn đã ủng hộ quán ạ',5,'2026-05-18 00:08:18','PUBLISHED',1);
/*!40000 ALTER TABLE `reviews` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `roles`
--

DROP TABLE IF EXISTS `roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `roles` (
  `role_id` smallint unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`role_id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `roles`
--

LOCK TABLES `roles` WRITE;
/*!40000 ALTER TABLE `roles` DISABLE KEYS */;
INSERT INTO `roles` VALUES (2,'ROLE_ADMIN'),(4,'ROLE_MANAGER'),(3,'ROLE_STAFF'),(1,'ROLE_USER');
/*!40000 ALTER TABLE `roles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tables`
--

DROP TABLE IF EXISTS `tables`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tables` (
  `table_id` int unsigned NOT NULL AUTO_INCREMENT,
  `area_id` smallint unsigned NOT NULL,
  `restaurant_id` smallint unsigned NOT NULL,
  `table_number` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `capacity` tinyint unsigned DEFAULT '4',
  `status` enum('AVAILABLE','OCCUPIED','RESERVED','OUT_OF_SERVICE') COLLATE utf8mb4_unicode_ci DEFAULT 'AVAILABLE',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`table_id`),
  KEY `fk_tables_area` (`area_id`),
  KEY `fk_tables_restaurant` (`restaurant_id`),
  CONSTRAINT `fk_tables_area` FOREIGN KEY (`area_id`) REFERENCES `areas` (`area_id`) ON DELETE CASCADE,
  CONSTRAINT `fk_tables_restaurant` FOREIGN KEY (`restaurant_id`) REFERENCES `restaurants` (`restaurant_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tables`
--

LOCK TABLES `tables` WRITE;
/*!40000 ALTER TABLE `tables` DISABLE KEYS */;
INSERT INTO `tables` VALUES (1,1,1,'Bàn 01',4,'AVAILABLE','2026-04-20 00:53:58','2026-04-20 00:58:21'),(2,1,1,'Bàn 02',2,'AVAILABLE','2026-04-20 00:59:15','2026-04-20 00:59:15');
/*!40000 ALTER TABLE `tables` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_vouchers`
--

DROP TABLE IF EXISTS `user_vouchers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_vouchers` (
  `user_voucher_id` int unsigned NOT NULL AUTO_INCREMENT,
  `user_id` bigint unsigned NOT NULL,
  `voucher_id` int unsigned NOT NULL,
  `used_at` datetime DEFAULT NULL,
  `assigned_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_voucher_id`),
  KEY `voucher_id` (`voucher_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `user_vouchers_ibfk_1` FOREIGN KEY (`voucher_id`) REFERENCES `vouchers` (`voucher_id`),
  CONSTRAINT `user_vouchers_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_vouchers`
--

LOCK TABLES `user_vouchers` WRITE;
/*!40000 ALTER TABLE `user_vouchers` DISABLE KEYS */;
INSERT INTO `user_vouchers` VALUES (1,9,1,NULL,'2026-04-14 09:11:56');
/*!40000 ALTER TABLE `user_vouchers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `user_id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `rank_id` int DEFAULT NULL,
  `create_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `email` varchar(255) NOT NULL,
  `full_name` varchar(255) DEFAULT NULL,
  `password` varchar(255) NOT NULL,
  `phone` varchar(255) DEFAULT NULL,
  `points` int NOT NULL,
  `avatar` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `email` (`email`),
  KEY `FKp852f3y6fngvq7t4y6i57wv55` (`rank_id`),
  CONSTRAINT `FKp852f3y6fngvq7t4y6i57wv55` FOREIGN KEY (`rank_id`) REFERENCES `ranked` (`rank_id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (5,1,'2026-03-27 07:11:45','thiepthanhx03@gmail.com','Admin','$2a$10$Wa0DLwzVghNzBHBNGiQQWO8qTNWy1Vjbn1st.OHzb6bytIwrNIDZ6','0899073387',109,'/uploads/avatars/1c938b9e-46f6-423c-aa20-29dedd765706.jpg'),(7,1,'2026-03-27 07:54:22','quanly1@gmail.com','Pho Ham','$2a$10$b5E2j7RrSMZ2MLSWFdSAcufdipJ6tLcXaaudIJ1GAVr4nmXfGJcLi','0123456789',0,'/uploads/avatars/111a8f6d-c94e-4fcc-be70-5d14020b6237.jpg'),(9,1,'2026-04-10 03:26:17','dinhdat1995@gmail.com','Nguyen Dinh Thiep','$2a$10$HkS5tCYmi0x1UboJIOy.Vu3jQk04/fKOG9hF8t0BO1tQxiaYKpq2C','0749357699',260,'/uploads/avatars/c7bb0267-06ea-4dfe-ace1-df05548476c9.jpg'),(10,1,'2026-04-12 18:55:36','nhanvien1@gmail.com','Nhân Viên 1','$2a$10$l0ou65/jqz00OHhq4zjZ.OMOVT7vasEVIuQjshpfET4bn975jjHvG','0988822103',0,NULL),(11,1,'2026-05-13 03:02:32','thiepthanhx09@gmail','NGUYEN DINH THANG','$2a$10$B7DDOoQk/ZnViqI5CpyNh.O9us1Q.cNWIax5eD.BCizkEWEFF7Wpq','0387783398',50,NULL),(12,1,'2026-05-17 21:04:37','thanhnguyen@gmail.com','Nguyen Dinh Thanh','$2a$10$CJCpMb2PdzL/lQMnCPL/zegKfJMPHlvlu8znp43QI..OG3KPJTV0q','07748',50,NULL),(13,1,'2026-05-18 01:07:15','khach1@gmail.com','khach1','$2a$10$QpzrXqdkO2YqReXOoQAGvOuLJVDCVoyS0Wd1MNcKOKv6GDqa11Elq','0977894467',50,NULL);
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users_roles`
--

DROP TABLE IF EXISTS `users_roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users_roles` (
  `user_id` bigint unsigned NOT NULL,
  `role_id` smallint unsigned NOT NULL,
  `restaurant_id` smallint unsigned DEFAULT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`id`),
  KEY `fk_ur_user` (`user_id`),
  KEY `fk_ur_role` (`role_id`),
  KEY `fk_ur_restaurant` (`restaurant_id`),
  CONSTRAINT `fk_ur_restaurant` FOREIGN KEY (`restaurant_id`) REFERENCES `restaurants` (`restaurant_id`) ON DELETE CASCADE,
  CONSTRAINT `fk_ur_role` FOREIGN KEY (`role_id`) REFERENCES `roles` (`role_id`) ON DELETE CASCADE,
  CONSTRAINT `fk_ur_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users_roles`
--

LOCK TABLES `users_roles` WRITE;
/*!40000 ALTER TABLE `users_roles` DISABLE KEYS */;
INSERT INTO `users_roles` VALUES (5,2,NULL,7),(7,4,1,9),(9,1,NULL,11),(10,3,1,12),(11,1,NULL,13),(12,1,NULL,14),(13,1,NULL,15);
/*!40000 ALTER TABLE `users_roles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `voucher_restaurants`
--

DROP TABLE IF EXISTS `voucher_restaurants`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `voucher_restaurants` (
  `id` int NOT NULL AUTO_INCREMENT,
  `voucher_id` int unsigned NOT NULL,
  `restaurant_id` smallint unsigned NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_voucher_restaurant` (`voucher_id`,`restaurant_id`),
  KEY `fk_vr_restaurant` (`restaurant_id`),
  CONSTRAINT `fk_vr_restaurant` FOREIGN KEY (`restaurant_id`) REFERENCES `restaurants` (`restaurant_id`) ON DELETE CASCADE,
  CONSTRAINT `fk_vr_voucher` FOREIGN KEY (`voucher_id`) REFERENCES `vouchers` (`voucher_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `voucher_restaurants`
--

LOCK TABLES `voucher_restaurants` WRITE;
/*!40000 ALTER TABLE `voucher_restaurants` DISABLE KEYS */;
/*!40000 ALTER TABLE `voucher_restaurants` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `vouchers`
--

DROP TABLE IF EXISTS `vouchers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `vouchers` (
  `voucher_id` int unsigned NOT NULL AUTO_INCREMENT,
  `voucher_name` varchar(100) NOT NULL,
  `description` text,
  `discount_amount` decimal(10,2) NOT NULL COMMENT 'Số tiền giảm giá',
  `expiry_date` datetime NOT NULL COMMENT 'Ngày hết hạn',
  `usage_limit` int DEFAULT NULL COMMENT 'Giới hạn số lần sử dụng (NULL nếu không giới hạn)',
  `min_order_value` decimal(10,2) DEFAULT '0.00' COMMENT 'Giá trị đơn hàng tối thiểu để áp dụng',
  `apply_type` enum('ALL','SPECIFIC') NOT NULL DEFAULT 'ALL',
  PRIMARY KEY (`voucher_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `vouchers`
--

LOCK TABLES `vouchers` WRITE;
/*!40000 ALTER TABLE `vouchers` DISABLE KEYS */;
INSERT INTO `vouchers` VALUES (1,'MAGIAMGIA20K','Giảm giá 20k cho hoá đơn trị giá 200k',20000.00,'2026-05-30 17:00:00',100,200000.00,'ALL');
/*!40000 ALTER TABLE `vouchers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping events for database 'restaurant_website'
--

--
-- Dumping routines for database 'restaurant_website'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-05-19 18:03:19
