# 🎓 Student Management System (SMS)

A robust, enterprise-grade desktop application for managing university student records and academic course enrollments. 

Built as an academic project for the University of Rwanda, College of Science and Technology, this system utilizes a strict **Model-View-Controller (MVC)** architecture and emphasizes secure, defensive database operations.

## 🚀 The General Idea
The Student Management System (SMS) provides university administrators with a seamless graphical interface to perform complete CRUD (Create, Read, Update, Delete) operations. Rather than just making a basic database wrapper, this project focuses on professional software engineering principles: ensuring data integrity through SQL transactions, protecting against SQL injection, and providing a highly responsive "context-aware" user experience.

## ✨ Key Features
* **Model-View-Controller (MVC) Architecture:** Complete separation of backend database queries from frontend UI logic, making the codebase highly maintainable.
* **Context-Aware UI:** A smart master dashboard where action buttons (Add, Update, Delete) dynamically adapt their functionality based on the currently active tab.
* **Secure Authentication:** * Features a secure login portal utilizing Java `SwingWorker` threads to prevent UI freezing during database authentication.
  * Inputs are sanitized and passwords are encrypted using secure hashing algorithms.
* **Defensive Database Operations:** * Uses `PreparedStatement` exclusively to eliminate SQL injection vulnerabilities.  
  * Implements strict **SQL Transactions** during deletion events to handle foreign key constraints safely (e.g., dropping student grades before deleting a course to prevent database corruption).
* **Real-Time Data Filtering:** Dynamic search bars and slider controls allow administrators to instantly filter tables without relying on heavy, repetitive SQL calls.

## 🛠️ Tech Stack
* **Language:** Java (JDK 25)
* **GUI Framework:** Java Swing
* **Database:** MySQL
* **Driver:** MySQL Connector/J (JDBC)
* **IDE:** Apache NetBeans

## ⚙️ Installation & Setup
1. **Clone the repository:**
   ```bash
   git clone [https://github.com/your-username/SMS_Project.git](https://github.com/your-username/SMS_Project.git)
   ```
2. **Database Configuration:**
   * Create a local MySQL database named `university_db`.
   * Open your SQL client (e.g., MySQL Workbench, phpMyAdmin) and run the SQL script provided in the Database Schema section below.
   * Update the `DBConnection.java` file in the database package with your local MySQL username and password.

3. **Run the Application:**
   * Open the project in NetBeans.
   * Ensure the `mysql-connector-j` library is added to your project dependencies.
   * Run the `LoginPage.java` file to launch the application.
  
  ## 🗄️ Database Schema
Run this exact SQL script to generate the required tables for the application. The schema uses strict foreign key relationships to maintain data integrity between students, courses, and their grades.

```sql
CREATE DATABASE IF NOT EXISTS university_db;
USE university_db;

-- Table 1: Students
CREATE TABLE students (
    student_id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL
);

-- Table 2: Courses
CREATE TABLE courses (
    course_id VARCHAR(50) PRIMARY KEY,
    course_name VARCHAR(100) NOT NULL
);

-- Table 3: Enrollments (Junction Table for Grades)
CREATE TABLE enrollments (
    enrollment_id INT AUTO_INCREMENT PRIMARY KEY,
    student_id VARCHAR(50) NOT NULL,
    course_id VARCHAR(50) NOT NULL,
    mark DECIMAL(5,2) DEFAULT 0.00,
    FOREIGN KEY (student_id) REFERENCES students(student_id),
    FOREIGN KEY (course_id) REFERENCES courses(course_id)
);
```
## 🤝 Contributors
This project was collaboratively developed by:

* **sfsf02** - [GitHub Profile](https://github.com/sfsf02)
* **dative1** - [GitHub Profile](https://github.com/dative1)
