package com.chess_pairing.chess_tournament.controller;

import com.chess_pairing.chess_tournament.models.Match;
import com.chess_pairing.chess_tournament.models.Player;
import com.chess_pairing.chess_tournament.models.StandingDTO;
import com.chess_pairing.chess_tournament.repository.MatchRepository;
import com.chess_pairing.chess_tournament.repository.PlayerRepository;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;

@RestController
@RequestMapping("/api/standings")
public class StandingsController {

    @GetMapping("/calculate")
    public ArrayList<StandingDTO> getStandings(@RequestParam int tournamentId) {
        ArrayList<StandingDTO> standings = new ArrayList<>();
        
        try {
            PlayerRepository playerRepo = new PlayerRepository();
            MatchRepository matchRepo = new MatchRepository();
            
            ArrayList<Player> players = playerRepo.getPlayersByTournament(tournamentId);
            ArrayList<Match> matches = matchRepo.getAllMatchesByTournament(tournamentId);

            // 1. Initialize Standings List
            for (int i = 0; i < players.size(); i++) {
                Player p = players.get(i);
                standings.add(new StandingDTO(p.getId(), p.getName(), p.getRating()));
            }

            // 2. Process Matches using traditional loops
            for (int i = 0; i < matches.size(); i++) {
                Match m = matches.get(i);
                String result = m.getResult();
                
                if (result == null || result.equals("Pending")) continue;

                StandingDTO white = findStanding(standings, m.getWhitePlayerId());
                StandingDTO black = (m.getBlackPlayerId() != null) ? findStanding(standings, m.getBlackPlayerId()) : null;

                if (white != null) {
                    if (result.equals("1-0") || result.equals("BYE")) white.addWin();
                    else if (result.equals("0.5-0.5")) white.addDraw();
                    else if (result.equals("0-1")) white.addLoss();
                }

                if (black != null) {
                    if (result.equals("0-1")) black.addWin();
                    else if (result.equals("0.5-0.5")) black.addDraw();
                    else if (result.equals("1-0")) black.addLoss();
                }
            }

            // 3. Bubble Sort by Points (Descending), then Rating (Descending)
            int n = standings.size();
            for (int i = 0; i < n - 1; i++) {
                for (int j = 0; j < n - i - 1; j++) {
                    StandingDTO s1 = standings.get(j);
                    StandingDTO s2 = standings.get(j + 1);
                    
                    boolean swap = false;
                    if (s1.getPoints() < s2.getPoints()) {
                        swap = true;
                    } else if (s1.getPoints() == s2.getPoints() && s1.getRating() < s2.getRating()) {
                        swap = true;
                    }
                    
                    if (swap) {
                        standings.set(j, s2);
                        standings.set(j + 1, s1);
                    }
                }
            }

            // 4. Assign Ranks
            for (int i = 0; i < standings.size(); i++) {
                standings.get(i).setRank(i + 1);
            }

        } catch (Exception e) {
            System.out.println("Error calculating standings: " + e.getMessage());
        }
        
        return standings;
    }

    // Helper method to find a player in the list manually
    private StandingDTO findStanding(ArrayList<StandingDTO> list, Integer playerId) {
        if (playerId == null) return null;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getPlayerId() == playerId) {
                return list.get(i);
            }
        }
        return null;
    }
}