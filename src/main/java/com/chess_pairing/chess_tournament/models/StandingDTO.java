package com.chess_pairing.chess_tournament.models;

public class StandingDTO {
    private int rank;
    private int playerId;
    private String playerName;
    private int rating;
    private double points;
    private int wins;
    private int draws;
    private int losses;

    public StandingDTO(int playerId, String playerName, int rating) {
        this.playerId = playerId;
        this.playerName = playerName;
        this.rating = rating;
        this.points = 0.0;
        this.wins = 0;
        this.draws = 0;
        this.losses = 0;
    }

    // Getters and Setters
    public int getRank() { return rank; }
    public void setRank(int rank) { this.rank = rank; }

    public int getPlayerId() { return playerId; }
    public String getPlayerName() { return playerName; }
    public int getRating() { return rating; }

    public double getPoints() { return points; }

    public int getWins() { return wins; }
    public void addWin() { this.wins++; this.points += 1.0; }

    public int getDraws() { return draws; }
    public void addDraw() { this.draws++; this.points += 0.5; }

    public int getLosses() { return losses; }
    public void addLoss() { this.losses++; }
}