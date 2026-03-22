package com.chess_pairing.chess_tournament.repository;

import com.chess_pairing.chess_tournament.models.Tournament;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class TournamentRepository {

    public TournamentRepository() {
        DatabaseUtil.initializeSchema();
    }

    public void save(Tournament tournament) throws Exception {
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("INSERT INTO tournaments (name, format, total_rounds, status, tournament_date, location) VALUES (?, ?, ?, ?, ?, ?)") ) {

            pstmt.setString(1, tournament.getName());
            pstmt.setString(2, tournament.getFormat());
            pstmt.setInt(3, tournament.getTotalRounds());
            pstmt.setString(4, "Setup");
            pstmt.setString(5, tournament.getTournamentDate());
            pstmt.setString(6, tournament.getLocation());

            pstmt.executeUpdate();
        }
    }

    public ArrayList<Tournament> findAll() throws Exception {
        ArrayList<Tournament> list = new ArrayList<>();

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM tournaments ORDER BY id DESC");
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Tournament t = new Tournament();
                t.setId(rs.getInt("id"));
                t.setName(rs.getString("name"));
                t.setFormat(rs.getString("format"));
                t.setTotalRounds(rs.getInt("total_rounds"));
                t.setStatus(rs.getString("status"));
                t.setTournamentDate(rs.getString("tournament_date"));
                t.setLocation(rs.getString("location"));
                list.add(t);
            }
        }

        return list;
    }

    public Tournament findById(int id) throws Exception {
        Tournament t = null;

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM tournaments WHERE id = ?")) {

            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    t = new Tournament();
                    t.setId(rs.getInt("id"));
                    t.setName(rs.getString("name"));
                    t.setFormat(rs.getString("format"));
                    t.setTotalRounds(rs.getInt("total_rounds"));
                    t.setStatus(rs.getString("status"));
                    t.setTournamentDate(rs.getString("tournament_date"));
                    t.setLocation(rs.getString("location"));
                }
            }
        }

        return t;
    }

    public void deleteById(int id) throws Exception {
        try (Connection conn = DatabaseUtil.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement deleteTournament = conn.prepareStatement("DELETE FROM tournaments WHERE id = ?")) {
                deleteTournament.setInt(1, id);
                deleteTournament.executeUpdate();
                conn.commit();
            } catch (Exception e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }
}