package com.chess_pairing.chess_tournament.controller;

import com.chess_pairing.chess_tournament.models.Player;
import com.chess_pairing.chess_tournament.repository.PlayerRepository;
import com.chess_pairing.chess_tournament.repository.MatchRepository;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;

@RestController
@RequestMapping("/api/players")
public class PlayerController {
    @GetMapping("/import-locked")
    public Object isPlayerImportLocked(@RequestParam int tournamentId) {
        try {
            MatchRepository matchRepo = new MatchRepository();
            boolean round1Locked = false;
            try {
                var matches = matchRepo.getMatchesWithNames(tournamentId, 1);
                for (var m : matches) {
                    if (m.getResult() != null && !m.getResult().trim().isEmpty()) {
                        round1Locked = true;
                        break;
                    }
                }
            } catch (Exception e) {
                System.out.println("Warning: Could not check round 1 lock: " + e.getMessage());
            }
            return java.util.Collections.singletonMap("locked", round1Locked);
        } catch (Exception e) {
            System.out.println("Error checking import lock: " + e.getMessage());
            return java.util.Collections.singletonMap("error", "Failed to check import lock.");
        }
    }

    @PostMapping("/add")
    public String addPlayers(@RequestBody ArrayList<Player> players) {
        try {
            if (players == null || players.isEmpty()) {
                return "{\"error\": \"No players provided.\"}";
            }
            int tournamentId = players.get(0).getTournamentId();
            // Check if first round results are locked (any match in round 1 has a non-null result)
            MatchRepository matchRepo = new MatchRepository();
            boolean round1Locked = false;
            try {
                var matches = matchRepo.getMatchesWithNames(tournamentId, 1);
                for (var m : matches) {
                    if (m.getResult() != null && !m.getResult().trim().isEmpty()) {
                        round1Locked = true;
                        break;
                    }
                }
            } catch (Exception e) {
                // If error checking matches, default to allowing import
                System.out.println("Warning: Could not check round 1 lock: " + e.getMessage());
            }
            if (round1Locked) {
                return "{\"error\": \"Cannot import players after tournament has started (first round results locked).\"}";
            }
            PlayerRepository repo = new PlayerRepository();
            for (int i = 0; i < players.size(); i++) {
                repo.save(players.get(i));
            }
            return "{\"message\": \"Players imported successfully!\"}";
        } catch (Exception e) {
            System.out.println("Error saving players: " + e.getMessage());
            return "{\"error\": \"Failed to import players.\"}";
        }
    }

    @GetMapping("/by-tournament")
    public Object getPlayersByTournament(@RequestParam int tournamentId) {
        try {
            PlayerRepository repo = new PlayerRepository();
            return repo.getPlayersByTournament(tournamentId);
        } catch (Exception e) {
            System.out.println("Error fetching players: " + e.getMessage());
            return java.util.Collections.singletonMap("error", "Failed to load players.");
        }
    }

    @DeleteMapping("/delete")
    public String deletePlayer(@RequestParam int playerId) {
        try {
            PlayerRepository repo = new PlayerRepository();
            repo.deleteById(playerId);
            return "{\"message\": \"Player deleted successfully!\"}";
        } catch (Exception e) {
            System.out.println("Error deleting player: " + e.getMessage());
            return java.util.Collections.singletonMap("error", "Failed to delete player.").toString();
        }
    }
}