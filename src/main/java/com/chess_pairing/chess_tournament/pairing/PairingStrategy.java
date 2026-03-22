package com.chess_pairing.chess_tournament.pairing;

import com.chess_pairing.chess_tournament.models.Match;
import com.chess_pairing.chess_tournament.models.Player;
import java.util.ArrayList;

public interface PairingStrategy {
    // Takes the players and the history of all past matches to calculate the new round
    ArrayList<Match> generate(int tournamentId, int roundNum, ArrayList<Player> players, ArrayList<Match> pastMatches) throws Exception;
}