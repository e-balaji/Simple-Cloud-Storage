# ☁️ Simple Cloud Storage (SCS)

A lightweight, scalable file storage system inspired by Amazon S3, built using **Spring Boot** and **MySQL**, with support for user authentication, file uploads/downloads, metadata management, folder organization, access control (public/private), and versioning.

---

## 📦 Features

- ✅ User authentication with JWT
- 📁 Upload/download files to structured folders
- 📝 Metadata management (filename, visibility, owner, path)
- 🔐 File access control (private/public)
- 📄 File versioning
- 🧾 File listing per folder
- 📂 Folder-based organization
- ☁️ Dockerized for easy deployment

---

## 🚀 Getting Started

### 🔧 Prerequisites

- Java 17+
- Maven
- MySQL 8+
- Docker (optional but recommended)

---

### 🖥️ Running Locally

#### 1. Clone the Repository

git clone https://github.com/your-username/simple-cloud-storage.git
cd simple-cloud-storage

### 2. Set up MySQL
Create a database and user:

CREATE DATABASE scs_db;
CREATE USER 'scs_user'@'%' IDENTIFIED BY 'password';
GRANT ALL PRIVILEGES ON scs_db.* TO 'scs_user'@'%';
FLUSH PRIVILEGES;

### 3. Configure Application
Update src/main/resources/application.properties:

spring.datasource.url=jdbc:mysql://localhost:3306/scs_db
spring.datasource.username=scs_user
spring.datasource.password=password
jwt.secret=YourVerySecureSecretKeyHere
file.upload-dir=uploads

### 4. Build & Run
mvn clean install
java -jar target/scs-0.0.1-SNAPSHOT.jar

## Dockerized Deployment

### 1. Build Docker Image
docker build -t scs-app .

### 2. Run the App
docker run -d -p 8080:8080 --name scs-container scs-app

REST API Endpoints
🔐 Authentication
POST /api/auth/register – Register new user

POST /api/auth/login – Login and receive JWT

📁 File Management
POST /api/files/upload/{folder} – Upload file (with Authorization and visibility)

GET /api/files/list/{user}/{folder} – List files in folder

GET /api/files/download/{filename} – Download file (respecting access control)

🔐 Access Control
Each file has a Visibility:

PRIVATE – only accessible by owner

PUBLIC – accessible by anyone

🧱 Entity Relationship Diagram
![image](https://github.com/user-attachments/assets/9d5f1745-9b67-4eab-9736-411e14a41aa8)


🧪 Testing
You can use Postman or cURL to test all endpoints. Make sure to include Authorization: Bearer <your_jwt_token> in headers for protected routes.

✨ Future Improvements
Cloud storage integration (e.g., AWS S3, GCP)

File search and tagging

Email-based notifications

Admin dashboard


