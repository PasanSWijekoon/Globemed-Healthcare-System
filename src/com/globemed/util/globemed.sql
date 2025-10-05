-- --------------------------------------------------------
-- Host:                         127.0.0.1
-- Server version:               8.0.30 - MySQL Community Server - GPL
-- Server OS:                    Win64
-- HeidiSQL Version:             12.1.0.6537
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


-- Dumping database structure for globemed
CREATE DATABASE IF NOT EXISTS `globemed` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `globemed`;

-- Dumping structure for table globemed.appointments
CREATE TABLE IF NOT EXISTS `appointments` (
  `appointment_id` int NOT NULL AUTO_INCREMENT,
  `patient_id` varchar(20) NOT NULL,
  `staff_id` varchar(20) NOT NULL,
  `appointment_date` datetime NOT NULL,
  `status` varchar(20) NOT NULL DEFAULT 'Scheduled',
  `facility_id` int DEFAULT NULL,
  `appointment_type_id` int DEFAULT NULL,
  PRIMARY KEY (`appointment_id`),
  KEY `patient_id` (`patient_id`),
  KEY `staff_id` (`staff_id`),
  KEY `fk_appointments_facility` (`facility_id`),
  KEY `fk_appointments_type` (`appointment_type_id`),
  CONSTRAINT `appointments_ibfk_1` FOREIGN KEY (`patient_id`) REFERENCES `patients` (`patient_id`),
  CONSTRAINT `appointments_ibfk_2` FOREIGN KEY (`staff_id`) REFERENCES `staff` (`staff_id`),
  CONSTRAINT `fk_appointments_facility` FOREIGN KEY (`facility_id`) REFERENCES `facilities` (`facility_id`) ON DELETE SET NULL,
  CONSTRAINT `fk_appointments_type` FOREIGN KEY (`appointment_type_id`) REFERENCES `appointment_types` (`type_id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table globemed.appointments: ~14 rows (approximately)
INSERT INTO `appointments` (`appointment_id`, `patient_id`, `staff_id`, `appointment_date`, `status`, `facility_id`, `appointment_type_id`) VALUES
	(1, 'P-101', 'S-002', '2025-08-27 10:00:00', 'Completed', 2, 2),
	(2, 'P-102', 'S-002', '2025-08-27 11:00:00', 'Completed', 2, 5),
	(3, 'P-101', 'S-003', '2025-08-27 09:00:00', 'Completed', 2, 2),
	(4, 'P-102', 'S-002', '2025-08-29 11:00:00', 'Cancelled', 3, 2),
	(5, 'P-104', 'S-002', '2025-08-28 17:00:00', 'Completed', 2, 2),
	(6, 'P-105', 'S-002', '2025-08-28 09:00:00', 'Completed', 2, 2),
	(7, 'P-105', 'S-002', '2025-08-30 14:00:00', 'Cancelled', 1, 3),
	(8, 'P-102', 'S-002', '2025-08-30 15:00:00', 'Completed', 1, 5),
	(9, 'P-102', 'S-002', '2025-08-30 16:00:00', 'Cancelled', 1, 5),
	(10, 'P-102', 'S-002', '2025-08-30 18:00:00', 'Cancelled', 1, 1),
	(12, 'P-102', 'S-002', '2025-08-31 18:00:00', 'Scheduled', 1, 3),
	(13, 'P-101', 'S-002', '2025-08-30 06:00:00', 'Completed', 1, 3),
	(14, 'P-101', 'S-002', '2025-08-31 19:00:00', 'Scheduled', 1, 3),
	(15, 'P-101', 'S-002', '2025-08-30 07:00:00', 'Completed', 1, 5);

-- Dumping structure for table globemed.appointment_types
CREATE TABLE IF NOT EXISTS `appointment_types` (
  `type_id` int NOT NULL AUTO_INCREMENT,
  `type_name` varchar(50) NOT NULL,
  `duration_minutes` int DEFAULT '30',
  `description` text,
  PRIMARY KEY (`type_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table globemed.appointment_types: ~6 rows (approximately)
INSERT INTO `appointment_types` (`type_id`, `type_name`, `duration_minutes`, `description`) VALUES
	(1, 'General Consultation', 30, 'Standard medical consultation'),
	(2, 'Follow-up', 20, 'Follow-up appointment'),
	(3, 'Diagnostic Test', 60, 'Medical tests and diagnostics'),
	(4, 'Surgery Consultation', 45, 'Pre/post surgery consultation'),
	(5, 'Emergency', 15, 'Emergency appointment'),
	(6, 'Vaccination', 15, 'Vaccination appointment');

-- Dumping structure for table globemed.audit_log
CREATE TABLE IF NOT EXISTS `audit_log` (
  `log_id` int NOT NULL AUTO_INCREMENT,
  `log_timestamp` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `username` varchar(255) NOT NULL,
  `action_description` varchar(512) NOT NULL,
  PRIMARY KEY (`log_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table globemed.audit_log: ~0 rows (approximately)

-- Dumping structure for table globemed.facilities
CREATE TABLE IF NOT EXISTS `facilities` (
  `facility_id` int NOT NULL AUTO_INCREMENT,
  `facility_name` varchar(100) NOT NULL,
  `facility_type` enum('Hospital','Clinic','Pharmacy') NOT NULL,
  `address` text,
  PRIMARY KEY (`facility_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table globemed.facilities: ~3 rows (approximately)
INSERT INTO `facilities` (`facility_id`, `facility_name`, `facility_type`, `address`) VALUES
	(1, 'GlobeMed Central Hospital', 'Hospital', '123 Healthcare Ave, Medical District'),
	(2, 'GlobeMed Downtown Clinic', 'Clinic', '456 Main Street, Colombo'),
	(3, 'GlobeMed Pharmacy', 'Pharmacy', '789 Wellness Rd, Liberty Plaza');

-- Dumping structure for table globemed.insurance_claims
CREATE TABLE IF NOT EXISTS `insurance_claims` (
  `claim_id` int NOT NULL AUTO_INCREMENT,
  `invoice_id` int NOT NULL,
  `insurance_provider` varchar(50) DEFAULT NULL,
  `claim_status` varchar(20) NOT NULL DEFAULT 'Submitted',
  PRIMARY KEY (`claim_id`),
  KEY `invoice_id` (`invoice_id`),
  CONSTRAINT `invoice_id` FOREIGN KEY (`invoice_id`) REFERENCES `invoices` (`invoice_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table globemed.insurance_claims: ~3 rows (approximately)
INSERT INTO `insurance_claims` (`claim_id`, `invoice_id`, `insurance_provider`, `claim_status`) VALUES
	(3, 3, 'Ceylinco', 'Approved'),
	(4, 5, 'Ceylinco', 'Approved'),
	(5, 8, 'Ceylinco', 'Denied');

-- Dumping structure for table globemed.invoices
CREATE TABLE IF NOT EXISTS `invoices` (
  `invoice_id` int NOT NULL AUTO_INCREMENT,
  `patient_id` varchar(20) NOT NULL,
  `service_date` date DEFAULT NULL,
  `amount` decimal(10,2) NOT NULL,
  `status` varchar(20) NOT NULL DEFAULT 'Pending',
  `claim_id` int DEFAULT NULL,
  PRIMARY KEY (`invoice_id`),
  KEY `patient_id` (`patient_id`),
  KEY `claim_id` (`claim_id`),
  CONSTRAINT `invoices_ibfk_1` FOREIGN KEY (`patient_id`) REFERENCES `patients` (`patient_id`),
  CONSTRAINT `invoices_ibfk_2` FOREIGN KEY (`claim_id`) REFERENCES `insurance_claims` (`claim_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table globemed.invoices: ~8 rows (approximately)
INSERT INTO `invoices` (`invoice_id`, `patient_id`, `service_date`, `amount`, `status`, `claim_id`) VALUES
	(1, 'P-101', '2025-08-27', 150.00, 'Paid', NULL),
	(2, 'P-102', '2025-08-28', 150.00, 'Paid', NULL),
	(3, 'P-101', '2025-08-28', 150.00, 'Paid', 3),
	(4, 'P-104', '2025-08-28', 150.00, 'Paid', NULL),
	(5, 'P-105', '2025-08-28', 150.00, 'Paid', 4),
	(6, 'P-102', '2025-08-30', 150.00, 'Paid', NULL),
	(7, 'P-101', '2025-08-31', 150.00, 'Paid', NULL),
	(8, 'P-101', '2025-08-31', 150.00, 'Claimed', 5);

-- Dumping structure for table globemed.medical_history
CREATE TABLE IF NOT EXISTS `medical_history` (
  `id` int NOT NULL AUTO_INCREMENT,
  `patient_id` varchar(20) DEFAULT NULL,
  `diagnosis` text,
  `date_of_entry` date DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `patient_id` (`patient_id`),
  CONSTRAINT `medical_history_ibfk_1` FOREIGN KEY (`patient_id`) REFERENCES `patients` (`patient_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table globemed.medical_history: ~12 rows (approximately)
INSERT INTO `medical_history` (`id`, `patient_id`, `diagnosis`, `date_of_entry`) VALUES
	(1, 'P-101', 'Influenza', '2025-01-10'),
	(2, 'P-101', 'Asthma', '2024-03-20'),
	(3, 'P-102', 'Hypertension', '2023-11-05'),
	(5, 'P-101', 'Haemorrhoids', '2025-08-27'),
	(6, 'P-101', 'Haemorrhoids', '2025-08-27'),
	(7, 'P-102', 'Fever', '2025-08-28'),
	(8, 'P-101', 'Gastrictics', '2025-08-28'),
	(9, 'P-104', 'Heart Attack', '2025-08-28'),
	(10, 'P-105', 'Migrane', '2025-08-28'),
	(11, 'P-102', 'Fever', '2025-08-30'),
	(12, 'P-101', 'Fever', '2025-08-31'),
	(13, 'P-101', 'Fever', '2025-08-31');

-- Dumping structure for table globemed.patients
CREATE TABLE IF NOT EXISTS `patients` (
  `patient_id` varchar(20) NOT NULL,
  `f_name` varchar(50) NOT NULL,
  `l_name` varchar(50) NOT NULL,
  `birthday` date DEFAULT NULL,
  `registered_date` date DEFAULT NULL,
  `gender` varchar(10) DEFAULT NULL,
  `contact_number` varchar(15) DEFAULT NULL,
  `address` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`patient_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table globemed.patients: ~4 rows (approximately)
INSERT INTO `patients` (`patient_id`, `f_name`, `l_name`, `birthday`, `registered_date`, `gender`, `contact_number`, `address`) VALUES
	('P-101', 'Pooja', 'Umashankar', '1985-05-15', '2025-08-24', 'Female', '0716756778', 'Kirula Road, 05'),
	('P-102', 'Lasith', 'Malinga', '1990-10-22', '2025-08-24', 'Male', '0717890778', '378 Old Moor Street, 12'),
	('P-104', 'Ranil', 'Wickremesinghe', '1949-03-24', '2025-08-24', 'Male', '0718967557', 'NO.174, POLHENA'),
	('P-105', 'Mahinda', 'Rajapaksha', '1978-08-23', '2025-08-28', 'Male', '0716789778', '35/S 15 Main Street, 11');

-- Dumping structure for table globemed.staff
CREATE TABLE IF NOT EXISTS `staff` (
  `staff_id` varchar(20) NOT NULL,
  `f_name` varchar(50) NOT NULL,
  `l_name` varchar(50) NOT NULL,
  `role` varchar(20) NOT NULL,
  `contact_number` varchar(15) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`staff_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table globemed.staff: ~5 rows (approximately)
INSERT INTO `staff` (`staff_id`, `f_name`, `l_name`, `role`, `contact_number`, `email`) VALUES
	('S-001', 'Luffy', 'Silva', 'Admin', '0717687667', 'luffy.silva@globemed.com'),
	('S-002', 'Mohamed', 'Shafi', 'Doctor', '0765678556', 'Mohamed.Shafi @globemed.com'),
	('S-003', 'Yureni', 'Noshika', 'Nurse', '0786676536', 'Yureni@globemed.com'),
	('S-004', 'Nethmi', 'Roshel', 'Pharmacist', '0765367665', 'Roshels@globemed.com'),
	('S-005', 'Kumar', 'Sangakkara', 'Coordinator', '0725635446', 'Sangakkara@globemed.com');

-- Dumping structure for table globemed.treatment_plans
CREATE TABLE IF NOT EXISTS `treatment_plans` (
  `id` int NOT NULL AUTO_INCREMENT,
  `patient_id` varchar(20) DEFAULT NULL,
  `plan_details` text,
  `start_date` date DEFAULT NULL,
  `end_date` date DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `patient_id` (`patient_id`),
  CONSTRAINT `treatment_plans_ibfk_1` FOREIGN KEY (`patient_id`) REFERENCES `patients` (`patient_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table globemed.treatment_plans: ~12 rows (approximately)
INSERT INTO `treatment_plans` (`id`, `patient_id`, `plan_details`, `start_date`, `end_date`) VALUES
	(1, 'P-101', 'Prescribe antiviral medication', '2025-01-10', '2025-01-17'),
	(2, 'P-101', 'Daily inhaler usage', '2024-03-20', '2025-12-31'),
	(3, 'P-102', 'Blood pressure monitoring and diet plan', '2023-11-05', '2025-11-05'),
	(5, 'P-101', 'high-fiber diet', '2025-08-20', '2025-08-29'),
	(6, 'P-101', 'regular exercise to soften stools', '2025-08-27', '2025-08-29'),
	(7, 'P-102', 'Drink Panadole 3 Times', '2025-08-28', '2025-08-30'),
	(8, 'P-101', 'Omesprozole 100ml 2 times', '2025-08-28', '2025-08-29'),
	(9, 'P-104', 'Taking prescribed Nitroglycerin.', '2025-08-28', '2025-08-31'),
	(10, 'P-105', 'Pandole', '2025-08-28', '2025-08-29'),
	(11, 'P-102', 'Drink lots of clear liquids', '2025-08-30', '2025-08-31'),
	(12, 'P-101', 'Drink Panadole', '2025-08-31', '2025-09-01'),
	(13, 'P-101', 'Drink Panadene', '2025-08-31', '2025-09-01');

-- Dumping structure for table globemed.users
CREATE TABLE IF NOT EXISTS `users` (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(50) DEFAULT NULL,
  `password` varchar(100) DEFAULT NULL,
  `staff_id` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`),
  KEY `fk_staff_id` (`staff_id`),
  CONSTRAINT `fk_staff_id` FOREIGN KEY (`staff_id`) REFERENCES `staff` (`staff_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table globemed.users: ~5 rows (approximately)
INSERT INTO `users` (`id`, `username`, `password`, `staff_id`) VALUES
	(7, 'Luffy', 'password123', 'S-001'),
	(8, 'Shafi', 'password123', 'S-002'),
	(9, 'Yureni', 'password123', 'S-003'),
	(10, 'Nethmi', 'password123', 'S-004'),
	(11, 'Sangakkara', 'password123', 'S-005');

-- Dumping structure for table globemed.visits
CREATE TABLE IF NOT EXISTS `visits` (
  `visit_id` int NOT NULL AUTO_INCREMENT,
  `patient_id` varchar(20) DEFAULT NULL,
  `visit_date` datetime DEFAULT NULL,
  `notes` text,
  PRIMARY KEY (`visit_id`),
  KEY `patient_id` (`patient_id`),
  CONSTRAINT `visits_ibfk_1` FOREIGN KEY (`patient_id`) REFERENCES `patients` (`patient_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table globemed.visits: ~12 rows (approximately)
INSERT INTO `visits` (`visit_id`, `patient_id`, `visit_date`, `notes`) VALUES
	(1, 'P-101', '2025-01-10 09:30:00', 'Patient presented with flu-like symptoms.'),
	(2, 'P-102', '2023-11-05 10:45:00', 'Initial consultation for high blood pressure.'),
	(3, 'P-101', '2025-08-20 14:00:00', 'Follow-up appointment for asthma.'),
	(5, 'P-101', '2025-08-27 13:15:02', 'Rectal bleeding'),
	(6, 'P-101', '2025-08-27 13:57:30', 'Rectal bleeding'),
	(7, 'P-102', '2025-08-28 01:21:22', 'Persistent fever'),
	(8, 'P-101', '2025-08-28 01:44:22', 'Stomach Pain'),
	(9, 'P-104', '2025-08-28 17:13:47', 'Extream Heart Pain'),
	(10, 'P-105', '2025-08-28 18:50:34', 'High Headache'),
	(11, 'P-102', '2025-08-30 15:35:58', 'Severe headache'),
	(12, 'P-101', '2025-08-31 02:59:31', 'Stiff neck'),
	(13, 'P-101', '2025-08-31 03:33:58', 'Shortness of breath');

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
