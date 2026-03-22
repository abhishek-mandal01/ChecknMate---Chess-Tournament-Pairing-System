package com.chess_pairing.chess_tournament.controller;

import com.chess_pairing.chess_tournament.models.Tournament;
import com.chess_pairing.chess_tournament.repository.TournamentRepository;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;

@RestController
@RequestMapping("/api/tournaments")
public class TournamentController {

    @PostMapping("/create")
    public String createTournament(@RequestBody Tournament tournament) {
        try {
            // Manual instantiation to avoid Dependency Injection
            TournamentRepository repo = new TournamentRepository();
            repo.save(tournament);
            return "{\"message\": \"Tournament created successfully!\"}";
        } catch (Exception e) {
            System.out.println("Error saving tournament: " + e.getMessage());
            return "{\"error\": \"Failed to create tournament.\"}";
        }
    }

    @GetMapping("/all")
    public ArrayList<Tournament> getAllTournaments() {
        try {
            TournamentRepository repo = new TournamentRepository();
            return repo.findAll();
        } catch (Exception e) {
            System.out.println("Error fetching tournaments: " + e.getMessage());
            return new ArrayList<>(); // Return empty array list on failure
        }
    }

    @DeleteMapping("/delete")
    public String deleteTournament(@RequestParam int tournamentId) {
        try {
            TournamentRepository repo = new TournamentRepository();
            Tournament tournament = repo.findById(tournamentId);

            if (tournament == null) {
                return "{\"error\": \"Tournament not found.\"}";
            }

            repo.deleteById(tournamentId);
            return "{\"message\": \"Tournament deleted successfully!\"}";
        } catch (Exception e) {
            System.out.println("Error deleting tournament: " + e.getMessage());
            return "{\"error\": \"Failed to delete tournament.\"}";
        }
    }
}