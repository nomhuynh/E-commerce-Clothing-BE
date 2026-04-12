/*
SQLyog Community v13.3.1 (64 bit)
MySQL - 8.0.44 : Database - stylex_spring
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`stylex_spring` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `stylex_spring`;

/*Table structure for table `addresses` */

DROP TABLE IF EXISTS `addresses`;

CREATE TABLE `addresses` (
  `address_id` varchar(255) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `deleted_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `city` varchar(255) NOT NULL,
  `district` varchar(255) NOT NULL,
  `is_default` bit(1) DEFAULT NULL,
  `phone_number` varchar(255) NOT NULL,
  `recipient_name` varchar(255) NOT NULL,
  `street_address` varchar(255) NOT NULL,
  `ward` varchar(255) NOT NULL,
  `user_id` varchar(255) NOT NULL,
  PRIMARY KEY (`address_id`),
  KEY `FK1fa36y2oqhao3wgg2rw1pi459` (`user_id`),
  CONSTRAINT `FK1fa36y2oqhao3wgg2rw1pi459` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Table structure for table `cart_items` */

DROP TABLE IF EXISTS `cart_items`;

CREATE TABLE `cart_items` (
  `cart_item_id` varchar(255) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `deleted_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `quantity` int NOT NULL,
  `cart_id` varchar(255) NOT NULL,
  `variant_id` varchar(255) NOT NULL,
  PRIMARY KEY (`cart_item_id`),
  UNIQUE KEY `uk_cart_variant` (`cart_id`,`variant_id`),
  KEY `FK5yyw1o0dor9gmxfra1dqvn4qa` (`variant_id`),
  CONSTRAINT `FK5yyw1o0dor9gmxfra1dqvn4qa` FOREIGN KEY (`variant_id`) REFERENCES `product_variants` (`variant_id`),
  CONSTRAINT `FKpcttvuq4mxppo8sxggjtn5i2c` FOREIGN KEY (`cart_id`) REFERENCES `carts` (`cart_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Table structure for table `carts` */

DROP TABLE IF EXISTS `carts`;

CREATE TABLE `carts` (
  `cart_id` varchar(255) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `deleted_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `session_id` varchar(255) DEFAULT NULL,
  `coupon_id` varchar(255) DEFAULT NULL,
  `user_id` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`cart_id`),
  UNIQUE KEY `UK1ihnigm9j28oe6n2qkws6q13q` (`session_id`),
  UNIQUE KEY `UK64t7ox312pqal3p7fg9o503c2` (`user_id`),
  KEY `FKb4abp6oso8eo5bqb5y0ggnfl5` (`coupon_id`),
  CONSTRAINT `FKb4abp6oso8eo5bqb5y0ggnfl5` FOREIGN KEY (`coupon_id`) REFERENCES `coupons` (`coupon_id`),
  CONSTRAINT `FKb5o626f86h46m4s7ms6ginnop` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Table structure for table `categories` */

DROP TABLE IF EXISTS `categories`;

CREATE TABLE `categories` (
  `category_id` varchar(255) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `deleted_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `image` varchar(255) DEFAULT NULL,
  `is_active` bit(1) DEFAULT NULL,
  `name` varchar(100) NOT NULL,
  `slug` varchar(150) NOT NULL,
  `parent_id` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`category_id`),
  UNIQUE KEY `UKoul14ho7bctbefv8jywp5v3i2` (`slug`),
  KEY `FKsaok720gsu4u2wrgbk10b5n8d` (`parent_id`),
  CONSTRAINT `FKsaok720gsu4u2wrgbk10b5n8d` FOREIGN KEY (`parent_id`) REFERENCES `categories` (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Table structure for table `colors` */

DROP TABLE IF EXISTS `colors`;

CREATE TABLE `colors` (
  `color_id` varchar(255) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `deleted_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `hex_code` varchar(7) DEFAULT NULL,
  `name` varchar(50) NOT NULL,
  PRIMARY KEY (`color_id`),
  UNIQUE KEY `UKkfulqa7c70otb7t3uwkgcpy43` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Table structure for table `coupon_usages` */

DROP TABLE IF EXISTS `coupon_usages`;

CREATE TABLE `coupon_usages` (
  `usage_id` varchar(255) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `deleted_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `discount_amount` decimal(15,2) NOT NULL,
  `order_id` varchar(255) NOT NULL,
  `used_at` datetime(6) NOT NULL,
  `coupon_id` varchar(255) NOT NULL,
  `user_id` varchar(255) NOT NULL,
  PRIMARY KEY (`usage_id`),
  KEY `FK3mvslb8gc0ac6501mfmvifgva` (`coupon_id`),
  KEY `FK6mev6grxbqmt8l0jxvobfg70n` (`user_id`),
  CONSTRAINT `FK3mvslb8gc0ac6501mfmvifgva` FOREIGN KEY (`coupon_id`) REFERENCES `coupons` (`coupon_id`),
  CONSTRAINT `FK6mev6grxbqmt8l0jxvobfg70n` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Table structure for table `coupons` */

DROP TABLE IF EXISTS `coupons`;

CREATE TABLE `coupons` (
  `coupon_id` varchar(255) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `deleted_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `code` varchar(50) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `discount_type` enum('fixed_amount','percentage') NOT NULL,
  `discount_value` decimal(15,2) NOT NULL,
  `end_date` datetime(6) NOT NULL,
  `is_active` bit(1) DEFAULT NULL,
  `max_discount_amount` decimal(15,2) DEFAULT NULL,
  `min_order_value` decimal(15,2) DEFAULT NULL,
  `per_user_limit` int DEFAULT NULL,
  `start_date` datetime(6) NOT NULL,
  `usage_count` int DEFAULT NULL,
  `usage_limit` int DEFAULT NULL,
  PRIMARY KEY (`coupon_id`),
  UNIQUE KEY `UKeplt0kkm9yf2of2lnx6c1oy9b` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Table structure for table `device_tokens` */

DROP TABLE IF EXISTS `device_tokens`;

CREATE TABLE `device_tokens` (
  `device_token_id` varchar(255) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `deleted_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `is_active` bit(1) NOT NULL,
  `platform` enum('ANDROID','IOS','WEB') NOT NULL,
  `token` varchar(512) NOT NULL,
  `user_id` varchar(255) NOT NULL,
  PRIMARY KEY (`device_token_id`),
  UNIQUE KEY `uk_device_token_user_token` (`user_id`,`token`),
  CONSTRAINT `FKhc7d11bnr8x9gs5biohdhnx1c` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Table structure for table `inventories` */

DROP TABLE IF EXISTS `inventories`;

CREATE TABLE `inventories` (
  `inventory_id` varchar(255) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `deleted_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `on_hand` int NOT NULL,
  `reserved` int NOT NULL,
  `variant_id` varchar(255) NOT NULL,
  `warehouse_id` varchar(255) NOT NULL,
  PRIMARY KEY (`inventory_id`),
  UNIQUE KEY `unique_inventory_per_warehouse_variant` (`warehouse_id`,`variant_id`),
  KEY `FK1hwr2nkixleellrw6uthechom` (`variant_id`),
  CONSTRAINT `FK1hwr2nkixleellrw6uthechom` FOREIGN KEY (`variant_id`) REFERENCES `product_variants` (`variant_id`),
  CONSTRAINT `FKoipfe4s81wodvutx9i0rlmoyi` FOREIGN KEY (`warehouse_id`) REFERENCES `warehouses` (`warehouse_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Table structure for table `loyalty_transactions` */

DROP TABLE IF EXISTS `loyalty_transactions`;

CREATE TABLE `loyalty_transactions` (
  `loyalty_transaction_id` varchar(255) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `deleted_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `order_total_amount` decimal(15,2) DEFAULT NULL,
  `points` int NOT NULL,
  `transaction_type` enum('EARN_DELIVERED','REVERSE') NOT NULL,
  `order_id` varchar(255) NOT NULL,
  `user_id` varchar(255) NOT NULL,
  PRIMARY KEY (`loyalty_transaction_id`),
  KEY `FK52ee4932gargabsnvpqv9roio` (`order_id`),
  KEY `FK6abe0rsidtoc0x44b02rwyl34` (`user_id`),
  CONSTRAINT `FK52ee4932gargabsnvpqv9roio` FOREIGN KEY (`order_id`) REFERENCES `orders` (`order_id`),
  CONSTRAINT `FK6abe0rsidtoc0x44b02rwyl34` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Table structure for table `materials` */

DROP TABLE IF EXISTS `materials`;

CREATE TABLE `materials` (
  `material_id` varchar(255) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `deleted_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `description` text,
  `name` varchar(50) NOT NULL,
  PRIMARY KEY (`material_id`),
  UNIQUE KEY `UKp6b0ef7tpc5wohnygnl60qk4j` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Table structure for table `notifications` */

DROP TABLE IF EXISTS `notifications`;

CREATE TABLE `notifications` (
  `notification_id` varchar(255) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `deleted_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `content` text,
  `is_read` bit(1) NOT NULL,
  `reference_id` varchar(255) DEFAULT NULL,
  `reference_type` varchar(50) DEFAULT NULL,
  `title` varchar(255) NOT NULL,
  `type` enum('ORDER','PROMOTION','REVIEW','SYSTEM') NOT NULL,
  `user_id` varchar(255) NOT NULL,
  PRIMARY KEY (`notification_id`),
  KEY `FK9y21adhxn0ayjhfocscqox7bh` (`user_id`),
  CONSTRAINT `FK9y21adhxn0ayjhfocscqox7bh` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Table structure for table `order_items` */

DROP TABLE IF EXISTS `order_items`;

CREATE TABLE `order_items` (
  `order_item_id` varchar(255) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `deleted_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `product_name` varchar(255) NOT NULL,
  `quantity` int NOT NULL,
  `sku` varchar(255) NOT NULL,
  `total_price` decimal(15,2) NOT NULL,
  `unit_price` decimal(15,2) NOT NULL,
  `order_id` varchar(255) NOT NULL,
  `variant_id` varchar(255) NOT NULL,
  PRIMARY KEY (`order_item_id`),
  KEY `FKbioxgbv59vetrxe0ejfubep1w` (`order_id`),
  KEY `FKemq71edpbn9wsxnxncfn1algp` (`variant_id`),
  CONSTRAINT `FKbioxgbv59vetrxe0ejfubep1w` FOREIGN KEY (`order_id`) REFERENCES `orders` (`order_id`),
  CONSTRAINT `FKemq71edpbn9wsxnxncfn1algp` FOREIGN KEY (`variant_id`) REFERENCES `product_variants` (`variant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Table structure for table `orders` */

DROP TABLE IF EXISTS `orders`;

CREATE TABLE `orders` (
  `order_id` varchar(255) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `deleted_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `customer_name` varchar(255) NOT NULL,
  `customer_phone` varchar(255) NOT NULL,
  `discount_amount` decimal(15,2) DEFAULT NULL,
  `note` text,
  `order_code` varchar(255) NOT NULL,
  `order_status` enum('CANCELLED','CONFIRMED','DELIVERED','PENDING','RETURNED','SHIPPING') NOT NULL,
  `shipping_address` text NOT NULL,
  `shipping_fee` decimal(15,2) DEFAULT NULL,
  `subtotal_amount` decimal(15,2) DEFAULT NULL,
  `total_amount` decimal(15,2) NOT NULL,
  `user_id` varchar(255) DEFAULT NULL,
  `coupon_id` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`order_id`),
  UNIQUE KEY `UKdhk2umg8ijjkg4njg6891trit` (`order_code`),
  KEY `FK32ql8ubntj5uh44ph9659tiih` (`user_id`),
  KEY `FKn1d1gkxckw648m2n2d5gx0yx5` (`coupon_id`),
  CONSTRAINT `FK32ql8ubntj5uh44ph9659tiih` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`),
  CONSTRAINT `FKn1d1gkxckw648m2n2d5gx0yx5` FOREIGN KEY (`coupon_id`) REFERENCES `coupons` (`coupon_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Table structure for table `payments` */

DROP TABLE IF EXISTS `payments`;

CREATE TABLE `payments` (
  `payment_id` varchar(255) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `deleted_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `amount` decimal(15,2) NOT NULL,
  `payment_method` enum('COD','CREDIT_CARD','MOMO','STRIPE','VNPAY','ZALOPAY') NOT NULL,
  `payment_status` enum('FAILED','PENDING','REFUNDED','SUCCESS') NOT NULL,
  `payment_time` datetime(6) DEFAULT NULL,
  `transaction_code` varchar(255) DEFAULT NULL,
  `order_id` varchar(255) NOT NULL,
  PRIMARY KEY (`payment_id`),
  UNIQUE KEY `UK8vo36cen604as7etdfwmyjsxt` (`order_id`),
  CONSTRAINT `FK81gagumt0r8y3rmudcgpbk42l` FOREIGN KEY (`order_id`) REFERENCES `orders` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Table structure for table `product_images` */

DROP TABLE IF EXISTS `product_images`;

CREATE TABLE `product_images` (
  `image_id` varchar(255) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `deleted_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `alt_text` varchar(255) DEFAULT NULL,
  `image_url` text NOT NULL,
  `is_thumbnail` bit(1) DEFAULT NULL,
  `sort_order` int DEFAULT NULL,
  `color_id` varchar(255) DEFAULT NULL,
  `product_id` varchar(255) NOT NULL,
  PRIMARY KEY (`image_id`),
  KEY `FKqci3amfq4s9m0g7l3tipml59e` (`color_id`),
  KEY `FKqnq71xsohugpqwf3c9gxmsuy` (`product_id`),
  CONSTRAINT `FKqci3amfq4s9m0g7l3tipml59e` FOREIGN KEY (`color_id`) REFERENCES `colors` (`color_id`),
  CONSTRAINT `FKqnq71xsohugpqwf3c9gxmsuy` FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Table structure for table `product_usages` */

DROP TABLE IF EXISTS `product_usages`;

CREATE TABLE `product_usages` (
  `created_at` datetime(6) NOT NULL,
  `deleted_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `product_id` varchar(255) NOT NULL,
  `usage_id` varchar(255) NOT NULL,
  PRIMARY KEY (`product_id`,`usage_id`),
  KEY `FKlvxx34gumewvnwl2e2ge8resv` (`usage_id`),
  CONSTRAINT `FKg7g083ok7nv7hgay2rw2o2bj7` FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`),
  CONSTRAINT `FKlvxx34gumewvnwl2e2ge8resv` FOREIGN KEY (`usage_id`) REFERENCES `usages` (`usage_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Table structure for table `product_variants` */

DROP TABLE IF EXISTS `product_variants`;

CREATE TABLE `product_variants` (
  `variant_id` varchar(255) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `deleted_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `image` varchar(255) DEFAULT NULL,
  `is_active` bit(1) DEFAULT NULL,
  `original_price` decimal(15,2) DEFAULT NULL,
  `price` decimal(15,2) DEFAULT NULL,
  `sku` varchar(50) NOT NULL,
  `stock_quantity` int DEFAULT NULL,
  `weight` float DEFAULT NULL,
  `color_id` varchar(255) NOT NULL,
  `product_id` varchar(255) NOT NULL,
  `size_id` varchar(255) NOT NULL,
  PRIMARY KEY (`variant_id`),
  UNIQUE KEY `unique_product_variant_combination` (`product_id`,`color_id`,`size_id`),
  UNIQUE KEY `UKq935p2d1pbjm39n0063ghnfgn` (`sku`),
  KEY `FKnps1p21p470pq59fdj0ddwnrs` (`color_id`),
  KEY `FKt7j608wes333gojuoh0f8l488` (`size_id`),
  CONSTRAINT `FKnps1p21p470pq59fdj0ddwnrs` FOREIGN KEY (`color_id`) REFERENCES `colors` (`color_id`),
  CONSTRAINT `FKosqitn4s405cynmhb87lkvuau` FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`),
  CONSTRAINT `FKt7j608wes333gojuoh0f8l488` FOREIGN KEY (`size_id`) REFERENCES `sizes` (`size_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Table structure for table `products` */

DROP TABLE IF EXISTS `products`;

CREATE TABLE `products` (
  `product_id` varchar(255) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `deleted_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `age_group` enum('ADULT','BABY','KID','TEEN') NOT NULL,
  `base_price` decimal(15,2) NOT NULL,
  `description` text,
  `gender` enum('FEMALE','MALE','UNISEX') NOT NULL,
  `name` varchar(255) NOT NULL,
  `category_id` varchar(255) NOT NULL,
  `material_id` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`product_id`),
  KEY `FKog2rp4qthbtt2lfyhfo32lsw9` (`category_id`),
  KEY `FKj2d4f35svu15l83nru8t5k593` (`material_id`),
  CONSTRAINT `FKj2d4f35svu15l83nru8t5k593` FOREIGN KEY (`material_id`) REFERENCES `materials` (`material_id`),
  CONSTRAINT `FKog2rp4qthbtt2lfyhfo32lsw9` FOREIGN KEY (`category_id`) REFERENCES `categories` (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Table structure for table `promotion_products` */

DROP TABLE IF EXISTS `promotion_products`;

CREATE TABLE `promotion_products` (
  `created_at` datetime(6) NOT NULL,
  `deleted_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `product_id` varchar(255) NOT NULL,
  `promotion_id` varchar(255) NOT NULL,
  PRIMARY KEY (`product_id`,`promotion_id`),
  KEY `FKkn7hllhf1o8jjrolro4rqmxt7` (`promotion_id`),
  CONSTRAINT `FK9rm5m4rnoamh56kxetmoe1kk9` FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`),
  CONSTRAINT `FKkn7hllhf1o8jjrolro4rqmxt7` FOREIGN KEY (`promotion_id`) REFERENCES `promotions` (`promotion_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Table structure for table `promotions` */

DROP TABLE IF EXISTS `promotions`;

CREATE TABLE `promotions` (
  `promotion_id` varchar(255) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `deleted_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `description` text,
  `discount_type` enum('fixed_amount','percentage') NOT NULL,
  `discount_value` decimal(15,2) NOT NULL,
  `end_date` datetime(6) NOT NULL,
  `is_active` bit(1) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `start_date` datetime(6) NOT NULL,
  PRIMARY KEY (`promotion_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Table structure for table `reviews` */

DROP TABLE IF EXISTS `reviews`;

CREATE TABLE `reviews` (
  `review_id` varchar(255) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `deleted_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `content` text,
  `rating` int NOT NULL,
  `status` enum('APPROVED','PENDING','REJECTED') NOT NULL,
  `title` varchar(255) DEFAULT NULL,
  `product_id` varchar(255) NOT NULL,
  `user_id` varchar(255) NOT NULL,
  `images` json DEFAULT NULL,
  `is_verified` bit(1) NOT NULL,
  PRIMARY KEY (`review_id`),
  UNIQUE KEY `uk_review_user_product` (`user_id`,`product_id`),
  KEY `FKpl51cejpw4gy5swfar8br9ngi` (`product_id`),
  CONSTRAINT `FKcgy7qjc1r99dp117y9en6lxye` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`),
  CONSTRAINT `FKpl51cejpw4gy5swfar8br9ngi` FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`),
  CONSTRAINT `reviews_chk_1` CHECK (((`rating` <= 5) and (`rating` >= 1)))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Table structure for table `shipping_method_zones` */

DROP TABLE IF EXISTS `shipping_method_zones`;

CREATE TABLE `shipping_method_zones` (
  `id` varchar(255) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `deleted_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `estimated_days` int DEFAULT NULL,
  `fee` decimal(10,2) NOT NULL,
  `shipping_method_id` varchar(255) NOT NULL,
  `shipping_zone_id` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_shipping_method_zone` (`shipping_method_id`,`shipping_zone_id`),
  KEY `FK8fxcl4ugoko2au28ie7f8avsk` (`shipping_zone_id`),
  CONSTRAINT `FK8fxcl4ugoko2au28ie7f8avsk` FOREIGN KEY (`shipping_zone_id`) REFERENCES `shipping_zones` (`shipping_zone_id`),
  CONSTRAINT `FKp0xbpag206swk07rd4ffp9prv` FOREIGN KEY (`shipping_method_id`) REFERENCES `shipping_methods` (`shipping_method_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Table structure for table `shipping_methods` */

DROP TABLE IF EXISTS `shipping_methods`;

CREATE TABLE `shipping_methods` (
  `shipping_method_id` varchar(255) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `deleted_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `base_fee` decimal(10,2) DEFAULT NULL,
  `description` text,
  `estimated_days` int DEFAULT NULL,
  `is_active` bit(1) DEFAULT NULL,
  `name` varchar(100) NOT NULL,
  `free_shipping_threshold` decimal(10,2) DEFAULT NULL,
  PRIMARY KEY (`shipping_method_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Table structure for table `shipping_zones` */

DROP TABLE IF EXISTS `shipping_zones`;

CREATE TABLE `shipping_zones` (
  `shipping_zone_id` varchar(255) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `deleted_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `is_active` bit(1) DEFAULT NULL,
  `name` varchar(100) NOT NULL,
  `region` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`shipping_zone_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Table structure for table `sizes` */

DROP TABLE IF EXISTS `sizes`;

CREATE TABLE `sizes` (
  `size_id` varchar(255) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `deleted_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `name` varchar(10) NOT NULL,
  `type` enum('accessory','clothing','footwear') NOT NULL,
  PRIMARY KEY (`size_id`),
  UNIQUE KEY `unique_size_name_type` (`name`,`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Table structure for table `stock_movements` */

DROP TABLE IF EXISTS `stock_movements`;

CREATE TABLE `stock_movements` (
  `movement_id` varchar(255) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `deleted_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `movement_type` enum('ADJUST','IN','OUT','RELEASE','RESERVE','RETURN') NOT NULL,
  `note` text,
  `quantity` int NOT NULL,
  `reference_id` varchar(255) DEFAULT NULL,
  `reference_type` enum('MANUAL','ORDER','PURCHASE','RETURN') DEFAULT NULL,
  `variant_id` varchar(255) NOT NULL,
  `warehouse_id` varchar(255) NOT NULL,
  PRIMARY KEY (`movement_id`),
  KEY `FKo3rdw1xgft64g9fr8d3rnbt1q` (`variant_id`),
  KEY `FKiparp4rp4rsfsxb9y02oyxauh` (`warehouse_id`),
  CONSTRAINT `FKiparp4rp4rsfsxb9y02oyxauh` FOREIGN KEY (`warehouse_id`) REFERENCES `warehouses` (`warehouse_id`),
  CONSTRAINT `FKo3rdw1xgft64g9fr8d3rnbt1q` FOREIGN KEY (`variant_id`) REFERENCES `product_variants` (`variant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Table structure for table `usages` */

DROP TABLE IF EXISTS `usages`;

CREATE TABLE `usages` (
  `usage_id` varchar(255) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `deleted_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `description` text,
  `name` varchar(50) NOT NULL,
  PRIMARY KEY (`usage_id`),
  UNIQUE KEY `UKsqg52oiuyxl8s8u3wjyv9kjtn` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Table structure for table `users` */

DROP TABLE IF EXISTS `users`;

CREATE TABLE `users` (
  `user_id` varchar(255) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `deleted_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `auth_provider` enum('GOOGLE','LOCAL') DEFAULT NULL,
  `auth_provider_id` varchar(255) DEFAULT NULL,
  `avatar_url` text,
  `date_of_birth` date DEFAULT NULL,
  `email` varchar(255) NOT NULL,
  `email_verification_expires` datetime(6) DEFAULT NULL,
  `email_verification_token` varchar(255) DEFAULT NULL,
  `first_name` varchar(50) NOT NULL,
  `gender` enum('FEMALE','MALE','OTHER') DEFAULT NULL,
  `is_email_verified` bit(1) DEFAULT NULL,
  `last_login_at` datetime(6) DEFAULT NULL,
  `last_name` varchar(50) NOT NULL,
  `loyalty_points` int DEFAULT NULL,
  `password_hash` varchar(255) DEFAULT NULL,
  `phone_number` varchar(15) DEFAULT NULL,
  `preferences` json DEFAULT NULL,
  `reset_password_expires` datetime(6) DEFAULT NULL,
  `reset_password_token` varchar(255) DEFAULT NULL,
  `role` enum('ADMIN','CUSTOMER','STAFF') NOT NULL,
  `status` enum('ACTIVE','BANNED','PENDING') NOT NULL,
  `tier_level` enum('BRONZE','DIAMOND','GOLD','SILVER') DEFAULT NULL,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `UK6dotkott2kjsp8vw4d0m25fb7` (`email`),
  UNIQUE KEY `UK9q63snka3mdh91as4io72espi` (`phone_number`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Table structure for table `warehouses` */

DROP TABLE IF EXISTS `warehouses`;

CREATE TABLE `warehouses` (
  `warehouse_id` varchar(255) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `deleted_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `address` text,
  `code` varchar(50) NOT NULL,
  `is_active` bit(1) NOT NULL,
  `name` varchar(255) NOT NULL,
  `phone` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`warehouse_id`),
  UNIQUE KEY `UK6herdbg4x5wp6gkor8epv73oc` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Table structure for table `wishlists` */

DROP TABLE IF EXISTS `wishlists`;

CREATE TABLE `wishlists` (
  `wishlist_id` varchar(255) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `deleted_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `product_id` varchar(255) NOT NULL,
  `user_id` varchar(255) NOT NULL,
  PRIMARY KEY (`wishlist_id`),
  UNIQUE KEY `uk_wishlist_user_product` (`user_id`,`product_id`),
  KEY `FKl7ao98u2bm8nijc1rv4jobcrx` (`product_id`),
  CONSTRAINT `FK330pyw2el06fn5g28ypyljt16` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`),
  CONSTRAINT `FKl7ao98u2bm8nijc1rv4jobcrx` FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
