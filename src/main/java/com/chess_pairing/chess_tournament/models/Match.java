package com.chess_pairing.chess_tournament.models;

public class Match {
    private int id;
    private int tournamentId;
    private int roundNum;
    private int boardNum;
    private Integer whitePlayerId; // Integer allows null for BYEs
    private Integer blackPlayerId; 
    private String result; // "Pending", "1-0", "0-1", "0.5-0.5", "BYE"

    public Match() {}

    public Match(int tournamentId, int roundNum, int boardNum, Integer whitePlayerId, Integer blackPlayerId) {
        this.tournamentId = tournamentId;
        this.roundNum = roundNum;
        this.boardNum = boardNum;
        this.whitePlayerId = whitePlayerId;
        this.blackPlayerId = blackPlayerId;
        this.result = (blackPlayerId == null) ? "BYE" : "Pending";
    }

    // Standard Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getTournamentId() { return tournamentId; }
    public void setTournamentId(int tournamentId) { this.tournamentId = tournamentId; }

    public int getRoundNum() { return roundNum; }
    public void setRoundNum(int roundNum) { this.roundNum = roundNum; }

    public int getBoardNum() { return boardNum; }
    public void setBoardNum(int boardNum) { this.boardNum = boardNum; }

    public Integer getWhitePlayerId() { return whitePlayerId; }
    public void setWhitePlayerId(Integer whitePlayerId) { this.whitePlayerId = whitePlayerId; }

    public Integer getBlackPlayerId() { return blackPlayerId; }
    public void setBlackPlayerId(Integer blackPlayerId) { this.blackPlayerId = blackPlayerId; }

    public String getResult() { return result; }
    public void setResult(String result) { this.result = result; }
}