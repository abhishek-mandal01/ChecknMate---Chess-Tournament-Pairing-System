package com.chess_pairing.chess_tournament.pairing;

import com.chess_pairing.chess_tournament.models.Match;
import com.chess_pairing.chess_tournament.models.Player;
import java.util.ArrayList;

public class KnockoutPairing implements PairingStrategy {

    @Override
    public ArrayList<Match> generate(int tournamentId, int roundNum, ArrayList<Player> players, ArrayList<Match> pastMatches) throws Exception {
        ArrayList<Match> newMatches = new ArrayList<>();
        ArrayList<Player> activePlayers = new ArrayList<>();

        if (roundNum == 1) {
            // Round 1: Everyone is active, sort by rating
            for (int i = 0; i < players.size(); i++) {
                activePlayers.add(players.get(i));
            }
            
            for (int i = 0; i < activePlayers.size() - 1; i++) {
                for (int j = 0; j < activePlayers.size() - i - 1; j++) {
                    if (activePlayers.get(j).getRating() < activePlayers.get(j + 1).getRating()) {
                        Player temp = activePlayers.get(j);
                        activePlayers.set(j, activePlayers.get(j + 1));
                        activePlayers.set(j + 1, temp);
                    }
                }
            }
            
            // Seed matching (1 vs 16, 2 vs 15)
            int boardCount = 1;
            int half = activePlayers.size() / 2;
            for (int i = 0; i < half; i++) {
                Player white = activePlayers.get(i);
                Player black = activePlayers.get(activePlayers.size() - 1 - i);
                newMatches.add(new Match(tournamentId, roundNum, boardCount++, white.getId(), black.getId()));
            }
            // Handle odd man out
            if (activePlayers.size() % 2 != 0) {
                newMatches.add(new Match(tournamentId, roundNum, boardCount, activePlayers.get(half).getId(), null));
            }

        } else {
            // Subsequent Rounds: Find winners from the previous round
            for (int i = 0; i < pastMatches.size(); i++) {
                Match m = pastMatches.get(i);
                if (m.getRoundNum() == roundNum - 1) {
                    if (m.getResult().equals("1-0") || m.getResult().equals("BYE")) {
                        activePlayers.add(getPlayerById(players, m.getWhitePlayerId()));
                    } else if (m.getResult().equals("0-1")) {
                        activePlayers.add(getPlayerById(players, m.getBlackPlayerId()));
                    }
                    // Draws are rare in strict knockout, but usually decided by tiebreakers. 
                    // For this scope, we skip them or treat as double elimination.
                }
            }

            // Pair adjacent winners
            int boardCount = 1;
            for (int i = 0; i < activePlayers.size(); i += 2) {
                Player white = activePlayers.get(i);
                Player black = null;
                if (i + 1 < activePlayers.size()) {
                    black = activePlayers.get(i + 1);
                }
                
                Integer bId = (black != null) ? black.getId() : null;
                newMatches.add(new Match(tournamentId, roundNum, boardCount++, white.getId(), bId));
            }
        }

        return newMatches;
    }

    private Player getPlayerById(ArrayList<Player> players, Integer id) {
        if (id == null) return null;
        for (int i = 0; i < players.size(); i++) {
            if (players.get(i).getId() == id) return players.get(i);
        }
        return null;
    }
}