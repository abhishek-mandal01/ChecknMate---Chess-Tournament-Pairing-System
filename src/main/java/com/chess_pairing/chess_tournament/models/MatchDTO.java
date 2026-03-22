package com.chess_pairing.chess_tournament.models;

public class MatchDTO {
    private int matchId;
    private int boardNum;
    private String whiteName;
    private int whiteRating;
    private String blackName;
    private int blackRating;
    private String result;
    private boolean isBye;
    private int roundNum;

    public MatchDTO() {}

    // Getters and Setters
    public int getMatchId() { return matchId; }
    public void setMatchId(int matchId) { this.matchId = matchId; }

    public int getBoardNum() { return boardNum; }
    public void setBoardNum(int boardNum) { this.boardNum = boardNum; }

    public String getWhiteName() { return whiteName; }
    public void setWhiteName(String whiteName) { this.whiteName = whiteName; }

    public int getWhiteRating() { return whiteRating; }
    public void setWhiteRating(int whiteRating) { this.whiteRating = whiteRating; }

    public String getBlackName() { return blackName; }
    public void setBlackName(String blackName) { this.blackName = blackName; }

    public int getBlackRating() { return blackRating; }
    public void setBlackRating(int blackRating) { this.blackRating = blackRating; }

    public String getResult() { return result; }
    public void setResult(String result) { this.result = result; }

    public boolean isBye() { return isBye; }
    public void setBye(boolean isBye) { this.isBye = isBye; }

    public int getRoundNum() { return roundNum; }
    public void setRoundNum(int roundNum) { this.roundNum = roundNum; }
}