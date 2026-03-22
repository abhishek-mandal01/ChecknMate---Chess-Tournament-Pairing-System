package com.chess_pairing.chess_tournament.controller;

import com.chess_pairing.chess_tournament.models.Match;
import com.chess_pairing.chess_tournament.models.Player;
import com.chess_pairing.chess_tournament.models.Tournament;
import com.chess_pairing.chess_tournament.pairing.*;
import com.chess_pairing.chess_tournament.repository.MatchRepository;
import com.chess_pairing.chess_tournament.repository.PlayerRepository;
import com.chess_pairing.chess_tournament.repository.TournamentRepository;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;

@RestController
@RequestMapping("/api/pairings")
public class PairingController {

    @PostMapping("/generate")
    public String generatePairings(@RequestParam int tournamentId, @RequestParam int roundNum) {
        try {
            TournamentRepository tRepo = new TournamentRepository();
            PlayerRepository playerRepo = new PlayerRepository();
            MatchRepository matchRepo = new MatchRepository();

            // 1. Get the specific tournament to check its format
            Tournament tournament = tRepo.findById(tournamentId);
            if (tournament == null) {
                return "{\"error\": \"Tournament not found.\"}";
            }

            if (roundNum < 1) {
                return "{\"error\": \"Round number must be at least 1.\"}";
            }

            if (roundNum > tournament.getTotalRounds()) {
                return "{\"error\": \"Round exceeds tournament total rounds (" + tournament.getTotalRounds() + ").\"}";
            }

            // Prevent generating the same round more than once.
            if (!matchRepo.getMatchesWithNames(tournamentId, roundNum).isEmpty()) {
                return "{\"error\": \"Round " + roundNum + " already has pairings generated.\"}";
            }

            // For round > 1, ensure previous round exists and all results are completed.
            if (roundNum > 1) {
                ArrayList<Match> previousRoundMatches = new ArrayList<>();
                ArrayList<Match> allMatches = matchRepo.getAllMatchesByTournament(tournamentId);

                for (int i = 0; i < allMatches.size(); i++) {
                    Match m = allMatches.get(i);
                    if (m.getRoundNum() == roundNum - 1) {
                        previousRoundMatches.add(m);
                    }
                }

                if (previousRoundMatches.isEmpty()) {
                    return "{\"error\": \"Generate and complete round " + (roundNum - 1) + " before generating round " + roundNum + ".\"}";
                }

                for (int i = 0; i < previousRoundMatches.size(); i++) {
                    String result = previousRoundMatches.get(i).getResult();
                    if (result == null || result.trim().isEmpty() || result.equalsIgnoreCase("Pending")) {
                        return "{\"error\": \"All matches in round " + (roundNum - 1) + " must have results before generating round " + roundNum + ".\"}";
                    }
                }
            }

            ArrayList<Player> players = playerRepo.getPlayersByTournament(tournamentId);
            if (players.size() < 2) {
                return "{\"error\": \"Not enough players to generate pairings.\"}";
            }

            ArrayList<Match> pastMatches = matchRepo.getAllMatchesByTournament(tournamentId);

            // 2. Polymorphism: Select the strategy based on the database value
            PairingStrategy strategy;
            String format = tournament.getFormat();

            if (format.equalsIgnoreCase("Round Robin")) {
                strategy = new RoundRobinPairing();
            } else if (format.equalsIgnoreCase("Knockout")) {
                strategy = new KnockoutPairing();
            } else {
                strategy = new SwissPairing(); // Default to Swiss
            }

            // 3. Execute the algorithm
            ArrayList<Match> newMatches = strategy.generate(tournamentId, roundNum, players, pastMatches);

            // 4. Save the results
            for (int i = 0; i < newMatches.size(); i++) {
                matchRepo.save(newMatches.get(i));
            }

            return "{\"message\": \"Round " + roundNum + " pairings generated using " + format + " algorithm!\"}";

        } catch (Exception e) {
            System.out.println("Error generating pairings: " + e.getMessage());
            return "{\"error\": \"Failed to generate pairings.\"}";
        }
    }
}