# ChatApp Backend

Backend for a **real-time chat application**, providing user authentication, JWT-based login, registration, WebSocket messaging, and notifications. Works with a TypeScript frontend.

---

## Features

- **User Registration & Login** with JWT authentication  
- **Global Chat**: Broadcast messages to all online users  
- **Private Messaging**: One-to-one chat between users  
- **Real-time Notifications** using WebSockets  
- **Secure API Endpoints** for all protected actions  

---

## Tech Stack

- **Backend Framework**: Spring Boot  
- **Database**: MySQL 
- **Security**: Spring Security + JWT  
- **Real-time Messaging**: WebSockets  
- **Language**: Java 17  

---

## API Endpoints

### Authentication

| Method | Endpoint       | Description                 |
|--------|----------------|-----------------------------|
| POST   | `/api/auth/register` |Register new user      |
| POST   | `/api/auth/login`    |Login user and getJWT  |

### Messaging

| Method | Endpoint        | Description                                         |
|--------|-----------------|----------------------------------                   |
| GET    | `/api/messages/global` | Fetch global messages                        |
| GET    | `/api/messages/private/{userId}` | Fetch private messages with a user |

> WebSocket endpoint: `/ws/chat` for sending/receiving real-time messages

---

## Getting Started

### Prerequisites

- Java 17+  
- Maven or Gradle  
- Database (MySQL/PostgreSQL/H2)  

---

#
