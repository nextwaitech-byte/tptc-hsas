# HSAS Java — Human Security Assessment System

Angular + Spring Boot + PostgreSQL recreation of `tptc_demo`, isolated from everything already running on this PC.

**Does not touch:** XAMPP Apache (:80), MySQL (:3306), the existing `tptc_demo` PHP app, or the host PostgreSQL service on **:5432**.

This stack uses its own Docker network and:

| Service | Port on this PC | Notes |
|---|---|---|
| Angular frontend | **4200** | http://localhost:4200 |
| Spring Boot API | **8088** | http://localhost:8088/api/v1 |
| PostgreSQL (Docker only) | **5433** | database `tptc_hsas` — not the host Postgres |

## Run from GitHub (any PC)

GitHub stores the code. It does **not** host the live HSAS website. To run the system, clone and start Docker:

```powershell
git clone https://github.com/nextwaitech-byte/tptc-hsas.git
cd tptc-hsas
docker compose up --build
```

Then open **http://localhost:4200** on that computer.

A public internet URL (for other people without Docker) needs a server such as a VPS, Railway, or Render — not GitHub Pages.

## Start (this PC)

Install Docker Desktop. From this folder:

```powershell
cd C:\xampp\htdocs\tptc_java
docker compose up --build
```

Wait until `tptc_java_api` and `tptc_java_web` are healthy, then open:

**http://localhost:4200**

Login (same demo accounts as `tptc_demo`):

| Email | Password | Role |
|---|---|---|
| admin@tptc.go.tz | Admin@123 | Super Admin |
| officer.kigoma@tptc.go.tz | Admin@123 | Post Officer |
| security@tptc.go.tz | Admin@123 | Security Officer |

Stop without deleting data:

```powershell
docker compose stop
```

Remove only this project's containers/volume (still does not touch host Postgres):

```powershell
docker compose down
```

## How the parts talk

```
Browser (Angular :4200)
    -> HTTP JSON  /api/v1/*
Spring Boot (Java :8088)
    -> JDBC
PostgreSQL Docker (:5433 -> 5432 inside container)
```

The frontend never talks to the database. JWT is used instead of PHP sessions.

## Frameworks

- Frontend: **Angular 19**
- Backend: **Spring Boot 3.4** (Web, Security, Data JPA, Validation)
- DB migrations: **Flyway**
- Database: **PostgreSQL 16** (Docker)

## Local frontend against Docker API

If the API is already up and you only want to work on Angular:

```powershell
cd frontend
npm start
```

`proxy.conf.json` forwards `/api` to `http://localhost:8088`.

## Project layout

```
backend/     Spring Boot REST API
frontend/    Angular SPA
docker-compose.yml
```
