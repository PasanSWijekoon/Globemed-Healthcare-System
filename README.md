# GlobeMed Healthcare Management System

![Java](https://img.shields.io/badge/Java-11-blue)
![Swing](https://img.shields.io/badge/UI-Java%20Swing-orange)
![MySQL](https://img.shields.io/badge/Database-MySQL-blue)
![Design Patterns](https://img.shields.io/badge/Architecture-Design%20Patterns-brightgreen)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

A comprehensive, integrated healthcare management system built with Java and powered by object-oriented design patterns. This desktop application is designed to centralize and streamline core administrative and clinical functions for healthcare organizations.

## 📜 Project Overview

GlobeMed is designed to address the challenges of fragmented and outdated IT systems in healthcare. It provides a unified platform for patient record management, appointment scheduling, and billing, enhancing data accessibility, simplifying workflows, and strengthening security. The system's architecture is fundamentally shaped by the strategic implementation of key object-oriented design patterns, making it a robust, scalable, and maintainable solution.

This project was developed as a submission for the Object-Oriented Design Patterns II examination.

## ✨ Features

* **Secure Patient Record Management**: Full CRUD operations for patient records with robust, role-based access control.
* **Appointment Scheduling**: An integrated module to manage appointments across different facilities and healthcare professionals.
* **Flexible Billing & Claims**: A decoupled system for handling direct billing and processing insurance claims.
* **Role-Based Access Control (RBAC)**: Securely manage permissions for different staff roles (Doctors, Nurses, Admins).
* **Medical Report Generation**: A flexible system to generate various text-based medical reports without modifying core data models.
* **Modern UI**: A clean and user-friendly interface built with Java Swing and the FlatLaf look and feel.

## 🛠️ Technology Stack

* **Programming Language**: Java 11
* **User Interface**: Java Swing Framework
* **Database**: MySQL
* **IDE**: Apache NetBeans
* **Key Libraries**:
    * [FlatLaf](https://www.formdev.com/flatlaf/) (for UI theme) 
    * [Swing-Chart](https://github.com/HanSolo/charts) (for dashboard analytics)
    * [MigLayout](http://www.miglayout.com/) (for UI layout)
    * [MySQL Connector/J](https://dev.mysql.com/downloads/connector/j/) (for database connectivity) 

## 🏗️ Design Patterns Utilized

The core of this project is the practical application of design patterns to solve real-world problems.

* **Proxy & Decorator**: Used for secure and extensible patient record management. The Proxy controls access, while the Decorator adds functionalities like logging and encryption. 
* **Command**: Decouples appointment scheduling requests from the system that processes them, allowing for flexible and extensible scheduling operations. 
* **Chain of Responsibility & State**: Manages the multi-step, procedural flow of billing and insurance claim processing in a modular and maintainable way. 
* **Strategy & Factory**: Implements a dynamic and role-based permission management system. The Factory creates the appropriate permission set (Strategy) for a given user role. 
* **Visitor**: Separates the report generation logic from the core data models (like PatientRecord), allowing for new report formats to be added easily without changing the data structure.

## 🚀 Getting Started

### Prerequisites

* Java Development Kit (JDK) 11 or higher
* Apache NetBeans IDE (recommended)
* MySQL Server

### Installation & Setup

1.  **Clone the repository:**
    ```sh
    git clone [https://github.com/your-username/globemed-healthcare-system.git](https://github.com/your-username/globemed-healthcare-system.git)
    ```
2.  **Database Setup:**
    * Create a new MySQL database named `globemed_db`.
    * Import the provided `database.sql` file to set up the required tables and initial data.
    * Update the database connection details in `src/com/globemed/util/DBUtil.java` with your MySQL username and password.

3.  **Open in NetBeans:**
    * Open Apache NetBeans.
    * Select `File > Open Project` and navigate to the cloned repository folder.
    * NetBeans will automatically detect the project and its libraries.

## Usage

1.  Build the project in NetBeans (`Run > Build Project`).
2.  Run the main application file (`src/com/globemed/application/Application.java`).
3.  Log in using one of the default credentials provided in the database setup.

## 📸 Screenshots

*(Add screenshots of your application here to showcase the UI)*

| Login Screen                               | Dashboard                                  |
| ------------------------------------------ | ------------------------------------------ |
| ![Login Screen](link-to-your-screenshot)   | ![Dashboard](link-to-your-screenshot)      |
| **Manage Patients** | **Appointment Scheduling** |
| ![Patients Screen](link-to-your-screenshot)| ![Appointments Screen](link-to-your-screenshot) |

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
