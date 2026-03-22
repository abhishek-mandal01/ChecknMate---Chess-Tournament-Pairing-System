package com.chess_pairing.chess_tournament.repository;

import com.chess_pairing.chess_tournament.models.Match;
import com.chess_pairing.chess_tournament.models.MatchDTO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class MatchRepository {
        public int getTournamentIdByMatchId(int matchId) throws Exception {
            int tournamentId = -1;
            try (Connection conn = DatabaseUtil.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement("SELECT tournament_id FROM matches WHERE id = ?")) {
                pstmt.setInt(1, matchId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        tournamentId = rs.getInt("tournament_id");
                    }
                }
            }
            return tournamentId;
        }
    public MatchRepository() {
        DatabaseUtil.initializeSchema();
    }

    public void save(Match match) throws Exception {
        try (Connection conn = DatabaseUtil.getConnection()) {
            // Check for duplicate match (same tournament, round, board)
            try (PreparedStatement checkStmt = conn.prepareStatement(
                "SELECT COUNT(*) FROM matches WHERE tournament_id = ? AND round_num = ? AND board_num = ?")) {
                checkStmt.setInt(1, match.getTournamentId());
                checkStmt.setInt(2, match.getRoundNum());
                checkStmt.setInt(3, match.getBoardNum());
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        // Duplicate found, do not insert
                        return;
                    }
                }
            }
            try (PreparedStatement pstmt = conn.prepareStatement("INSERT INTO matches (tournament_id, round_num, board_num, white_player_id, black_player_id, result) VALUES (?, ?, ?, ?, ?, ?)")) {
                pstmt.setInt(1, match.getTournamentId());
                pstmt.setInt(2, match.getRoundNum());
                pstmt.setInt(3, match.getBoardNum());
                pstmt.setObject(4, match.getWhitePlayerId());
                pstmt.setObject(5, match.getBlackPlayerId());
                pstmt.setString(6, match.getResult());
                pstmt.executeUpdate();
            }
        }
    }

    public ArrayList<MatchDTO> getMatchesWithNames(int tournamentId, int roundNum) throws Exception {
        ArrayList<MatchDTO> list = new ArrayList<>();

        String sql = "SELECT m.id as match_id, m.round_num, m.board_num, m.result, " +
                "pw.name as white_name, pw.rating as white_rating, " +
                "pb.name as black_name, pb.rating as black_rating " +
                "FROM matches m " +
                "LEFT JOIN players pw ON m.white_player_id = pw.id " +
                "LEFT JOIN players pb ON m.black_player_id = pb.id " +
                "WHERE m.tournament_id = ? AND m.round_num = ? " +
                "ORDER BY m.board_num ASC";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, tournamentId);
            pstmt.setInt(2, roundNum);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    MatchDTO dto = new MatchDTO();
                    dto.setMatchId(rs.getInt("match_id"));
                    dto.setRoundNum(rs.getInt("round_num"));
                    dto.setBoardNum(rs.getInt("board_num"));
                    dto.setResult(rs.getString("result"));

                    dto.setWhiteName(rs.getString("white_name"));
                    dto.setWhiteRating(rs.getInt("white_rating"));

                    String bName = rs.getString("black_name");
                    if (bName == null) {
                        dto.setBye(true);
                        dto.setBlackName("BYE");
                        dto.setBlackRating(0);
                    } else {
                        dto.setBye(false);
                        dto.setBlackName(bName);
                        dto.setBlackRating(rs.getInt("black_rating"));
                    }

                    list.add(dto);
                }
            }
        }

        return list;
    }

    public void updateResult(int matchId, String result) throws Exception {
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("UPDATE matches SET result = ? WHERE id = ?")) {

            pstmt.setString(1, result);
            pstmt.setInt(2, matchId);
            pstmt.executeUpdate();
        }
    }

    public String getResultByMatchId(int matchId) throws Exception {
        String result = null;

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT result FROM matches WHERE id = ?")) {

            pstmt.setInt(1, matchId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    result = rs.getString("result");
                }
            }
        }

        return result;
    }

    public ArrayList<Match> getAllMatchesByTournament(int tournamentId) throws Exception {
        ArrayList<Match> list = new ArrayList<>();

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM matches WHERE tournament_id = ?")) {

            pstmt.setInt(1, tournamentId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Match m = new Match();
                    m.setId(rs.getInt("id"));
                    m.setTournamentId(rs.getInt("tournament_id"));
                    m.setRoundNum(rs.getInt("round_num"));
                    m.setBoardNum(rs.getInt("board_num"));
                    m.setWhitePlayerId(rs.getObject("white_player_id") != null ? rs.getInt("white_player_id") : null);
                    m.setBlackPlayerId(rs.getObject("black_player_id") != null ? rs.getInt("black_player_id") : null);
                    m.setResult(rs.getString("result"));
                    list.add(m);
                }
            }
        }

        return list;
    }

    public ArrayList<MatchDTO> getAllMatchesWithNames(int tournamentId) throws Exception {
        ArrayList<MatchDTO> list = new ArrayList<>();

        String sql = "SELECT m.id as match_id, m.round_num, m.board_num, m.result, " +
                "pw.name as white_name, pw.rating as white_rating, " +
                "pb.name as black_name, pb.rating as black_rating " +
                "FROM matches m " +
                "LEFT JOIN players pw ON m.white_player_id = pw.id " +
                "LEFT JOIN players pb ON m.black_player_id = pb.id " +
                "WHERE m.tournament_id = ? " +
                "ORDER BY m.round_num ASC, m.board_num ASC";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, tournamentId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    MatchDTO dto = new MatchDTO();
                    dto.setMatchId(rs.getInt("match_id"));
                    dto.setRoundNum(rs.getInt("round_num"));
                    dto.setBoardNum(rs.getInt("board_num"));
                    dto.setResult(rs.getString("result"));

                    dto.setWhiteName(rs.getString("white_name"));
                    dto.setWhiteRating(rs.getInt("white_rating"));

                    String bName = rs.getString("black_name");
                    if (bName == null) {
                        dto.setBye(true);
                        dto.setBlackName("BYE");
                        dto.setBlackRating(0);
                    } else {
                        dto.setBye(false);
                        dto.setBlackName(bName);
                        dto.setBlackRating(rs.getInt("black_rating"));
                    }

                    list.add(dto);
                }
            }
        }

        return list;
    }
}