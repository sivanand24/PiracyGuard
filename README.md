PiracyGuard: Automated Sports Stream Detection
PiracyGuard is an intelligent, automated solution designed to detect and monitor unauthorized sports streaming content.
By combining powerful web crawling, real-time data processing, and a scalable microservices architecture,
this platform helps broadcasters and leagues protect their intellectual property.

🚀 The Problem
Digital sports piracy causes massive revenue losses for broadcasters and leagues. 
Manually searching for unauthorized streams is ineffective and impossible to scale.
Broadcasters need an automated, "always-on" solution to identify infringements across the web.

💡 The Solution
PiracyGuard automates the detection workflow:

Crawling: Uses SearXNG to aggregate search results across multiple web sources.

Backend Intelligence: A Spring Boot backend processes search metadata, filters potential matches, and stores findings in a secure database.

Invisible Watermarking System: A Feature developed to store invisible watermarked media with media's PHash + ownerEmail hashed into media 
                               and stored into database to initiate copyright claims

Deployment Ready: Containerized with Docker and deployed via cloud-native pipelines.

🏗️ Architecture
Code snippet
graph TD
    A[SearXNG Crawler] -->|Scrapes| B[Backend API (Spring Boot)]
    B -->|Persists Data| C[(PostgreSQL)]
    B -->|Exposes REST API| D[Dashboard/Client]
🛠️ Tech Stack
Backend: Java 17/21, Spring Boot 3.2.x, Hibernate/JPA.

Search Engine: SearXNG (Self-hosted container).

Database: PostgreSQL.

Containerization: Docker & Docker Compose.

Deployment: Railway CI/CD.

🚀 Live Demo
You can view the live application here:
👉 [piracyguard-production.up.railway.app]

💻 How to Run Locally
Prerequisites
Docker Desktop installed and running.

Java 17+ installed.

Steps
Clone the repository:

Bash
git clone https://github.com/your-username/crawler.git
cd crawler
Configure Environment:
Create a .env file in the root directory:

Code snippet
DB_USERNAME=your_db_user
DB_PASSWORD=your_db_password
Start the stack:

Bash
docker-compose up --build
Access the Application:

Backend: http://localhost:8080

Crawler Service: http://localhost:8081

📊 Project Structure
/src: Core Spring Boot application logic.

/docker-compose.yml: Infrastructure orchestration.

/Dockerfile: Multi-stage build definition for cloud deployment.

📝 License
This project is for educational purposes as part of the Google Solution Challenge 2026.
