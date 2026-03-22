package com.chess_pairing.chess_tournament.pairing;

import com.chess_pairing.chess_tournament.models.Match;
import com.chess_pairing.chess_tournament.models.Player;
import java.util.ArrayList;

public class SwissPairing implements PairingStrategy {

    // Helper class strictly for calculation
    private class PlayerStats {
        Player player;
        double points;
        ArrayList<Integer> playedOpponents;

        PlayerStats(Player p) {
            this.player = p;
            this.points = 0.0;
            this.playedOpponents = new ArrayList<>();
        }
    }

    @Override
    public ArrayList<Match> generate(int tournamentId, int roundNum, ArrayList<Player> players, ArrayList<Match> pastMatches) throws Exception {
        ArrayList<Match> newMatches = new ArrayList<>();
        ArrayList<PlayerStats> statsList = new ArrayList<>();

        // 1. Initialize stats for all players
        for (int i = 0; i < players.size(); i++) {
            statsList.add(new PlayerStats(players.get(i)));
        }

        // 2. Calculate current points and record played opponents using past matches
        for (int i = 0; i < pastMatches.size(); i++) {
            Match m = pastMatches.get(i);
            String res = m.getResult();
            if (res == null || res.equals("Pending")) continue;

            PlayerStats white = getStats(statsList, m.getWhitePlayerId());
            PlayerStats black = getStats(statsList, m.getBlackPlayerId());

            if (white != null && black != null) {
                white.playedOpponents.add(black.player.getId());
                black.playedOpponents.add(white.player.getId());

                if (res.equals("1-0")) { white.points += 1.0; }
                else if (res.equals("0-1")) { black.points += 1.0; }
                else if (res.equals("0.5-0.5")) { white.points += 0.5; black.points += 0.5; }
            } else if (white != null && m.getBlackPlayerId() == null) {
                // BYE
                white.points += 1.0; 
            }
        }

        // 3. Bubble Sort by Points (descending), then Rating (descending)
        int n = statsList.size();
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                PlayerStats s1 = statsList.get(j);
                PlayerStats s2 = statsList.get(j + 1);
                
                boolean swap = false;
                if (s1.points < s2.points) {
                    swap = true;
                } else if (s1.points == s2.points && s1.player.getRating() < s2.player.getRating()) {
                    swap = true;
                }

                if (swap) {
                    statsList.set(j, s2);
                    statsList.set(j + 1, s1);
                }
            }
        }

        // 4. Generate Pairings 
        boolean[] paired = new boolean[n];
        int boardCount = 1;

        for (int i = 0; i < n; i++) {
            if (paired[i]) continue;
            
            PlayerStats white = statsList.get(i);
            int opponentIndex = -1;

            // Try to find the closest player they haven't played yet
            for (int j = i + 1; j < n; j++) {
                if (!paired[j]) {
                    boolean alreadyPlayed = false;
                    for (int k = 0; k < white.playedOpponents.size(); k++) {
                        if (white.playedOpponents.get(k) == statsList.get(j).player.getId()) {
                            alreadyPlayed = true;
                            break;
                        }
                    }
                    if (!alreadyPlayed) {
                        opponentIndex = j;
                        break;
                    }
                }
            }

            // Fallback: If everyone left has been played, just play the next available person
            if (opponentIndex == -1) {
                for (int j = i + 1; j < n; j++) {
                    if (!paired[j]) {
                        opponentIndex = j;
                        break;
                    }
                }
            }

            if (opponentIndex != -1) {
                PlayerStats black = statsList.get(opponentIndex);
                newMatches.add(new Match(tournamentId, roundNum, boardCount, white.player.getId(), black.player.getId()));
                paired[i] = true;
                paired[opponentIndex] = true;
            } else {
                // Odd man out gets a BYE
                newMatches.add(new Match(tournamentId, roundNum, boardCount, white.player.getId(), null));
                paired[i] = true;
            }
            boardCount++;
        }

        return newMatches;
    }

    private PlayerStats getStats(ArrayList<PlayerStats> list, Integer id) {
        if (id == null) return null;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).player.getId() == id) return list.get(i);
        }
        return null;
    }
}