# PiracyGuard 
### Automated Sports Piracy Detection & Forensic Verification Platform

[![Java](https://img.shields.io/badge/Java-17-orange?style=flat-square&logo=java)](https://www.java.com)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen?style=flat-square&logo=springboot)](https://spring.io/projects/spring-boot)
[![Docker](https://img.shields.io/badge/Docker-Containerized-blue?style=flat-square&logo=docker)](https://www.docker.com)
[![Railway](https://img.shields.io/badge/Deployed-Railway-purple?style=flat-square)](https://railway.app)
[![Live Demo](https://img.shields.io/badge/Live-Demo-success?style=flat-square)](https://piracy-guard-rae66iyw9-sivanand24s-projects.vercel.app/Media.html)

> A Spring Boot backend that automates the discovery and forensic verification of illegal sports live-streams using perceptual hashing, invisible watermarking, and real-time web scraping.

---

## Live Links

| Resource | URL |
|----------|-----|
| Frontend Demo | [piracy-guard...vercel.app/Media.html](https://piracy-guard-rae66iyw9-sivanand24s-projects.vercel.app/Media.html) |
| Backend API | [piracyguard-1.onrender.com](https://piracyguard-1.onrender.com) |

---

## Overview

Sports piracy costs the industry billions annually. PiracyGuard automates the two hardest parts:

1. **Discovery** — finding illegal streams across the web in real time
2. **Forensic Verification** — proving a suspect stream is a copy of official content using perceptual hashing

This backend exposes a clean RESTful API that can be integrated with any rights-management or enforcement workflow.

---

## Key Features

- **Real-time stream discovery** — integrates a custom SearXNG search API to scrape suspect URLs from the web, with custom headers and environment config to bypass bot-protection and rate limits
- **Invisible watermarking** — embeds imperceptible watermarks into official media for traceability
- **Perceptual hashing (pHash)** — generates fingerprints of official and suspect media frames
- **Hamming distance calculation** — compares pHash values to quantify similarity and flag copyright violations with high accuracy
- **Forensic media vault** — stores verified official media alongside computed hashes for audit trails
- **RESTful API** — clean, documented endpoints for triggering scans, submitting media, and retrieving violation reports
- **Neon DB integration** — PostgreSQL-compatible cloud database for storing media records and scan results
- **Dockerized deployment** — full containerization for consistent, reproducible deployments on Railway

---

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Language | Java 17 |
| Framework | Spring Boot 3.x |
| Database | Neon DB (PostgreSQL) |
| Search Integration | SearXNG (custom API) |
| Media Processing | Perceptual Hashing (pHash), Watermarking |
| Containerization | Docker |
| Deployment | Railway (backend), Vercel (frontend) |
| Build Tool | Maven |

---

## Architecture

```
Client / Rights Holder
        │
        ▼
  Spring Boot API
  ┌─────────────────────────────────┐
  │  ScanController                 │
  │  ├── SearXNG Search Service     │  ← Discovers suspect streams
  │  ├── Media Watermark Service    │  ← Embeds invisible watermarks
  │  ├── pHash Engine               │  ← Fingerprints media frames
  │  ├── Hamming Distance Verifier  │  ← Flags violations
  │  └── Forensic Vault             │  ← Stores evidence
  └──────────────┬──────────────────┘
                 │
            Neon DB (PostgreSQL)
```

---

## Running Locally

### Prerequisites
- Java 17+
- Maven 3.8+
- Docker (optional, recommended)
- A Neon DB connection string

### 1. Clone the repo
```bash
git clone https://github.com/sivanand24/PiracyGuard.git
cd PiracyGuard
```

### 2. Configure environment
Create an `application.properties` or set environment variables:
```properties
spring.datasource.url=your_neon_db_url
spring.datasource.username=your_db_user
spring.datasource.password=your_db_password
SEARXNG_API_URL=your_searxng_instance_url
```

### 3. Build and run
```bash
# If you're using with Maven
mvn clean install
mvn spring-boot:run

# If you're using with Docker
docker build -t piracyguard .
docker run -p 8080:8080 --env-file .env piracyguard
```

### 4. Access
- API: `http://localhost:8080`
- Swagger UI (if enabled): `http://localhost:8080/swagger-ui/index.html`

---

##  API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/scan` | Trigger a piracy scan for a given media title |
| `POST` | `/api/media/upload` | Upload official media to the forensic vault |
| `GET`  | `/api/violations` | Retrieve all flagged violation reports |
| `GET`  | `/api/violations/{id}` | Get details of a specific violation |

---

## How Perceptual Hashing Works

Unlike cryptographic hashing (MD5, SHA), perceptual hashing generates a "fingerprint" based on the *visual content* of a media frame — not its bytes. Two visually similar frames produce hashes with a low **Hamming distance** (number of differing bits), even if the file has been re-encoded or re-compressed.

```
Official Frame  →  pHash: 101001011010...
Suspect Frame   →  pHash: 101001111010...
                           Hamming Distance: 2 → MATCH (likely pirated)
```

---

## Deployment

The backend is containerized with Docker and deployed on **Railway**. The frontend demo is hosted on **Vercel**.

---

## Author

**Sivanand Mishra**
- GitHub: [@sivanand24](https://github.com/sivanand24)
- LinkedIn: [sivanand-mishra](https://www.linkedin.com/in/sivanandh-mishra-6aba4123a/)
- Email: Sivanandmishra24@gmail.com
