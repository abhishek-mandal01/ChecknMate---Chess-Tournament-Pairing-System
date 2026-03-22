package com.chess_pairing.chess_tournament.models;

public class ResultRequest {
    private int matchId;
    private String result;

    public ResultRequest() {}

    public int getMatchId() { return matchId; }
    public void setMatchId(int matchId) { this.matchId = matchId; }

    public String getResult() { return result; }
    public void setResult(String result) { this.result = result; }
}