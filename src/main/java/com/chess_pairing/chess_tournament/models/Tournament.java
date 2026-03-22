package com.chess_pairing.chess_tournament.models;

public class Tournament {
    private int id;
    private String name;
    private String format;
    private int totalRounds;
    private String status;
    private String tournamentDate;
    private String location;

    // Empty constructor needed for Spring Boot JSON parsing
    public Tournament() {}

    // Standard Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getFormat() { return format; }
    public void setFormat(String format) { this.format = format; }

    public int getTotalRounds() { return totalRounds; }
    public void setTotalRounds(int totalRounds) { this.totalRounds = totalRounds; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getTournamentDate() { return tournamentDate; }
    public void setTournamentDate(String tournamentDate) { this.tournamentDate = tournamentDate; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
}