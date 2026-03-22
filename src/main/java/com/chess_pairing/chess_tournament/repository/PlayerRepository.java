package com.chess_pairing.chess_tournament.repository;

import com.chess_pairing.chess_tournament.models.Player;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;

public class PlayerRepository {
    public PlayerRepository() {
        DatabaseUtil.initializeSchema();
    }

    public void save(Player player) throws Exception {
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("INSERT INTO players (tournament_id, name, rating) VALUES (?, ?, ?)")) {

            pstmt.setInt(1, player.getTournamentId());
            pstmt.setString(2, player.getName());
            pstmt.setInt(3, player.getRating());
            pstmt.executeUpdate();
        }
    }

    public ArrayList<Player> getPlayersByTournament(int tournamentId) throws Exception {
        ArrayList<Player> list = new ArrayList<>();

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM players WHERE tournament_id = ?")) {

            pstmt.setInt(1, tournamentId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Player p = new Player();
                    p.setId(rs.getInt("id"));
                    p.setTournamentId(rs.getInt("tournament_id"));
                    p.setName(rs.getString("name"));
                    p.setRating(rs.getInt("rating"));
                    list.add(p);
                }
            }
        }

        return list;
    }

    public void deleteById(int playerId) throws Exception {
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("DELETE FROM players WHERE id = ?")) {
            pstmt.setInt(1, playerId);
            pstmt.executeUpdate();
        }
    }
}