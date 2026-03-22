package com.chess_pairing.chess_tournament.pairing;

import com.chess_pairing.chess_tournament.models.Match;
import com.chess_pairing.chess_tournament.models.Player;
import java.util.ArrayList;

public class RoundRobinPairing implements PairingStrategy {

    @Override
    public ArrayList<Match> generate(int tournamentId, int roundNum, ArrayList<Player> players, ArrayList<Match> pastMatches) throws Exception {
        ArrayList<Match> newMatches = new ArrayList<>();
        ArrayList<Player> rotatingList = new ArrayList<>();
        
        for (int i = 0; i < players.size(); i++) {
            rotatingList.add(players.get(i));
        }

        // Add dummy player for BYEs if odd number
        if (rotatingList.size() % 2 != 0) {
            rotatingList.add(null); 
        }

        int totalPlayers = rotatingList.size();
        Player fixedPlayer = rotatingList.get(0);
        
        ArrayList<Player> circle = new ArrayList<>();
        for (int i = 1; i < totalPlayers; i++) {
            circle.add(rotatingList.get(i));
        }

        // Rotate based on round number
        int rotations = (roundNum - 1) % circle.size();
        for (int r = 0; r < rotations; r++) {
            Player last = circle.get(circle.size() - 1);
            for (int i = circle.size() - 1; i > 0; i--) {
                circle.set(i, circle.get(i - 1));
            }
            circle.set(0, last);
        }

        int boardCount = 1;
        
        // Match fixed player with the last element of circle
        Player p2 = circle.get(circle.size() - 1);
        Integer p2Id = (p2 != null) ? p2.getId() : null;
        newMatches.add(new Match(tournamentId, roundNum, boardCount++, fixedPlayer.getId(), p2Id));

        // Match the rest symmetrically
        int halfSize = circle.size() / 2;
        for (int i = 0; i < halfSize; i++) {
            Player white = circle.get(i);
            Player black = circle.get(circle.size() - 2 - i);
            
            Integer wId = (white != null) ? white.getId() : null;
            Integer bId = (black != null) ? black.getId() : null;
            
            // Ensure white is always a valid player (swap if white is the dummy BYE slot)
            if (wId == null) {
                newMatches.add(new Match(tournamentId, roundNum, boardCount++, bId, null));
            } else {
                newMatches.add(new Match(tournamentId, roundNum, boardCount++, wId, bId));
            }
        }

        return newMatches;
    }
}