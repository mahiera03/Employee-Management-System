# 🏢 Employee Management System (Spring Boot + JWT + MySQL)

This is a **Spring Boot REST API** for managing employee records with **JWT Authentication** and **MySQL database integration**. The application includes user authentication, CRUD operations for employees, and security measures.

---

## **🚀 Features**
✅ User Authentication (Registration & Login)  
✅ Employee CRUD Operations (Create, Read, Update, Delete)  
✅ Pagination & Sorting for Employee Listing  
✅ Secure APIs using JWT Authentication  
✅ Global Exception Handling  
✅ Password Hashing with BCrypt  

---

## **🛠️ Tech Stack**
- **Backend**: Spring Boot, Spring Security, Spring Data JPA  
- **Database**: MySQL  
- **Authentication**: JWT (JSON Web Token)  
- **Validation**: Spring Boot Starter Validation  
- **Tools**: Maven, Postman (for testing API)  

---

## **📌 Project Setup**
### **1️⃣ Clone the Repository**
```sh
git clone https://github.com/mahiera03/Employee-Management-System
cd employee-management-system


2️⃣ Configure Database

Create a MySQL database:

CREATE DATABASE employee_db;

🌟Update src/main/resources/application.properties:

spring.datasource.url=jdbc:mysql://localhost:3306/employee_db
spring.datasource.username=root
spring.datasource.password=root

3️⃣ Build & Run the Application
mvn clean install
mvn spring-boot:run


📌 License
This project is open-source and available under the MIT License.

