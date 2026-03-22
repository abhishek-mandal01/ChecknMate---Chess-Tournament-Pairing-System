
# Check&Mate - Chess Tournament Pairing System

This is a web application for managing chess tournaments from start to finish. You can create tournaments, add players, generate pairings, enter results, and view standings, all in one place!

## What is this website for?

The website helps chess organizers run tournaments easily. You can:
- Set up new tournaments (choose format and rounds)
- Bulk import players
- Automatically generate pairings for each round (Swiss, Round Robin, Knockout)
- Enter and lock match results
- See live standings and full match history
- Export history as CSV

Everything is managed through a simple web interface—no manual pairing or spreadsheet work needed!

## How was it built?

- **Spring Initializr** was used to generate the base Spring Boot project.
- The app uses a **4-layer architecture**:
  1. **Controller Layer**: Handles REST API endpoints (e.g., `/api/tournaments/create`)
  2. **Repository Layer**: Direct JDBC access to PostgreSQL (no JPA/Hibernate)
  3. **Model Layer**: Plain Java classes (POJOs) for Tournament, Player, Match, etc.
  4. **Pairing Layer**: Implements different pairing algorithms (Swiss, Round Robin, Knockout)

## Tech Stack

- Java 17+
- Spring Boot 4 (generated with Spring Initializr)
- PostgreSQL (for storing all tournament data)
- HTML, CSS, JavaScript (frontend, no frameworks)
- Maven (build tool)

## Project Structure

```
src/main/java/com/chess_pairing/chess_tournament/
  controller/   # REST API endpoints
  repository/   # JDBC database access
  models/       # Data classes (POJOs)
  pairing/      # Pairing algorithms

src/main/resources/static/
  index.html, players.html, pairings.html, results.html, standings.html, history.html
  css/style.css, css/variables.css
  js/app.js
```

## How to Run

1. Make sure you have **JDK 17+** and **PostgreSQL** installed.
2. Clone the repo and open a terminal in the project folder.
3. Start the app:
   ```
   .\mvnw.cmd spring-boot:run
   ```
4. Open [http://localhost:8081](http://localhost:8081) in your browser.


## Database

- Tables are auto-created in PostgreSQL when the app runs.
- The production database is hosted on **Neon Technologies** (https://neon.tech).

## Deployment

- The website is hosted on **Vercel** (https://vercel.com), making it easy to access from anywhere.

## Why this approach?

- **Spring Initializr** makes it easy to set up a modern Java backend.
- The 4-layer structure keeps code organized and easy to understand.
- Using raw JDBC helps learn how database connections work under the hood.
- The frontend is kept simple and framework-free for learning purposes.


## License

This project is open for feedback and new ideas! If you have suggestions or want to contribute, feel free to open an issue or send a pull request (PR). Collaboration is encouraged and appreciated.