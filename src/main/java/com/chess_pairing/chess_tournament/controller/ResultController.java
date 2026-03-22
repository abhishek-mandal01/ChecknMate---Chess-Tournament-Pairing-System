package com.chess_pairing.chess_tournament.controller;

import com.chess_pairing.chess_tournament.repository.TournamentRepository;
import com.chess_pairing.chess_tournament.models.Tournament;

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
            MatchRepository matchRepo = new MatchRepository();
            TournamentRepository tournamentRepo = new TournamentRepository();

            // First pass: validate immutability so we avoid partial updates.
            for (int i = 0; i < results.size(); i++) {
                ResultRequest req = results.get(i);

                String existing = matchRepo.getResultByMatchId(req.getMatchId());
                if (existing == null) {
                    return "{\"error\": \"Match not found: " + req.getMatchId() + ".\"}";
                }

                String trimmedExisting = existing.trim();
                if (!trimmedExisting.isEmpty() &&
                    !trimmedExisting.equalsIgnoreCase("Pending") &&
                    !trimmedExisting.equalsIgnoreCase("BYE")) {
                    if (!trimmedExisting.equals(req.getResult())) {
                        return "{\"error\": \"Results are locked for match " + req.getMatchId() + ".\"}";
                    }
                }
            }

            // Second pass: apply updates when validations are successful.
            for (int i = 0; i < results.size(); i++) {
                ResultRequest req = results.get(i);
                matchRepo.updateResult(req.getMatchId(), req.getResult());
            }

            // After saving results, check if all rounds are completed for the tournament
            if (!results.isEmpty()) {
                int matchId = results.get(0).getMatchId();
                int tournamentId = matchRepo.getTournamentIdByMatchId(matchId);
                Tournament t = tournamentRepo.findById(tournamentId);
                if (t != null) {
                    int totalRounds = t.getTotalRounds();
                    boolean allRoundsCompleted = true;
                    for (int round = 1; round <= totalRounds; round++) {
                        var matches = matchRepo.getMatchesWithNames(tournamentId, round);
                        for (var m : matches) {
                            String r = m.getResult();
                            if (r == null || r.trim().isEmpty() || r.trim().equalsIgnoreCase("Pending")) {
                                allRoundsCompleted = false;
                                break;
                            }
                        }
                        if (!allRoundsCompleted) break;
                    }
                    if (allRoundsCompleted) {
                        tournamentRepo.updateStatus(tournamentId, "Completed");
                    }
                }
            }

            return "{\"message\": \"Results saved successfully!\"}";
        } catch (Exception e) {
            System.out.println("Error saving results: " + e.getMessage());
            return "{\"error\": \"Failed to save results.\"}";
        }
    }
}