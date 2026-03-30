# QuizRMI

Distributed multiplayer quiz game developed in Java using RMI.

## Overview

QuizRMI is a distributed application based on a client-server architecture that allows multiple players to participate in quiz matches. The system uses Java RMI (Remote Method Invocation) to enable communication between clients and the server.

The application supports multiple concurrent games, each managed independently by the server.

---

## Features

- Multiplayer quiz game
- Lobby system (create or join via ID)
- Turn-based gameplay
- Fixed game board (Jeopardy-style)
- Server-side answer validation (secure)
- Multiple matches handled concurrently
- Client callback support (asynchronous updates)

---

## Architecture

The system follows a **client-server model**:

### Server
- Manages game logic
- Handles multiple lobbies and matches
- Validates answers
- Maintains game state
- Communicates with clients via callbacks

### Client
- Provides user interface
- Sends requests to server
- Receives asynchronous updates via callbacks
- Implements part of the logic (not just passive)

## Distributed Logic

To ensure that the system is composed of actual distributed components, clients are not implemented as passive terminals.

Each client maintains a local representation of the current lobby and game state, including:
- visible game board
- current turn
- current question
- local UI state
- displayed scores

Clients also perform a minimal amount of application logic, such as:
- validating user actions before sending requests
- preventing invalid board selections
- preventing multiple answers to the same question
- handling local countdown visualization
- updating the interface in response to asynchronous server callbacks

The server remains authoritative for all critical decisions, including:
- board validation
- answer checking
- score updates
- turn management
- official game state
  
---

## Technologies

- Java
- Java RMI
- Distributed Objects
- Client-Server Architecture

---

## Communication Model

The system uses:

- **Remote interfaces (RMI)** for client-server communication
- **Callback mechanism** for server-to-client notifications

Example:
- Client → Server: join game, answer question
- Server → Client: game updates, turn changes, scores

---

## Game Flow

1. User connects to the server
2. A session is created (no persistent accounts)
3. Player can:
   - Create a lobby
   - Join a lobby via ID
4. Game starts when conditions are met
5. Players take turns selecting questions from the board
6. Server validates answers and updates scores
7. Game ends when all questions are used

---

## Game Design

- Board is fixed (categories + points)
- Players choose questions without seeing answers
- All validation is server-side (security)
- Each player has the same number of turns

---

## Design Choices

- No user registration (session-based system)
- No data persistence after match
- Server is authoritative (prevents cheating)
- Clients include logic to ensure distributed nature

---

## Project Structure
src/
├── common/
│ └── remote/ # RMI interfaces
├── server/ # Server logic and implementation
└── client/ # Client-side application

---

## How to Run

### 1. Start the server
Run: QuizServerMain

### 2. Start one or more clients
Run: QuizClientMain

---

## Future Improvements

- GUI (JavaFX or Swing)
- Timer for answers
- Scoreboard improvements
- Question database
- Difficulty scaling
- Peer-to-peer version (optional)

---

## Academic Context

This project was developed for the course:

**Distributed Algorithms (Algoritmi Distribuiti)**

It demonstrates:
- Distributed system design
- Remote communication (RMI)
- Concurrency management
- Client-server interaction

---

## Author

Matteo Grimelli
