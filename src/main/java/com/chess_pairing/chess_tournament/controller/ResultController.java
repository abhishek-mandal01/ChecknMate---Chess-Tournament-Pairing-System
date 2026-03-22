package com.chess_pairing.chess_tournament.controller;

import com.chess_pairing.chess_tournament.models.MatchDTO;
import com.chess_pairing.chess_tournament.models.ResultRequest;
import com.chess_pairing.chess_tournament.repository.MatchRepository;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;

@RestController
@RequestMapping("/api/results")
public class ResultController {

    @GetMapping("/round")
    public ArrayList<MatchDTO> getRoundMatches(@RequestParam int tournamentId, @RequestParam int roundNum) {
        try {
            MatchRepository repo = new MatchRepository();
            return repo.getMatchesWithNames(tournamentId, roundNum);
        } catch (Exception e) {
            System.out.println("Error fetching round matches: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @PostMapping("/submit")
    public String submitResults(@RequestBody ArrayList<ResultRequest> results) {
        try {
            MatchRepository repo = new MatchRepository();

            // First pass: validate immutability so we avoid partial updates.
            for (int i = 0; i < results.size(); i++) {
                ResultRequest req = results.get(i);

                String existing = repo.getResultByMatchId(req.getMatchId());
                if (existing == null) {
                    return "{\"error\": \"Match not found: " + req.getMatchId() + ".\"}";
                }

                String trimmedExisting = existing.trim();
                if (!trimmedExisting.isEmpty() && !trimmedExisting.equalsIgnoreCase("Pending")) {
                    if (!trimmedExisting.equals(req.getResult())) {
                        return "{\"error\": \"Results are locked for match " + req.getMatchId() + ".\"}";
                    }
                }
            }

            // Second pass: apply updates when validations are successful.
            for (int i = 0; i < results.size(); i++) {
                ResultRequest req = results.get(i);
                repo.updateResult(req.getMatchId(), req.getResult());
            }
            return "{\"message\": \"Results saved successfully!\"}";
        } catch (Exception e) {
            System.out.println("Error saving results: " + e.getMessage());
            return "{\"error\": \"Failed to save results.\"}";
        }
    }
}