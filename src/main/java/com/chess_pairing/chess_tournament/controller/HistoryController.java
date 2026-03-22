package com.chess_pairing.chess_tournament.controller;

import com.chess_pairing.chess_tournament.models.MatchDTO;
import com.chess_pairing.chess_tournament.repository.MatchRepository;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;

@RestController
@RequestMapping("/api/history")
public class HistoryController {

    @GetMapping("/all")
    public ArrayList<MatchDTO> getTournamentHistory(@RequestParam int tournamentId) {
        try {
            MatchRepository repo = new MatchRepository();
            return repo.getAllMatchesWithNames(tournamentId);
        } catch (Exception e) {
            System.out.println("Error fetching history: " + e.getMessage());
            return new ArrayList<>();
        }
    }
}