package com.iqb.league.model;

import lombok.Data;

@Data
public class DetailedTeamPoints {
    private int teamId;
    private int goalsScored;
    private int goalsConceded;
    private int matchesWon;
    private int matchesLost;
    private int matchesDrawn;
    private int goalDifference;
    private int overallScore;
    private int leagueId;

    // Gol averajını güncelleyen metot
    public void updateGoalDifference() {
        this.goalDifference = goalsScored - goalsConceded;
    }
    public void increaseMatchesWon() {
        this.matchesWon++;
    }
    public void increaseMatchesLost() {
        this.matchesLost++;
    }
    public void increaseMatchesDrawn() {
        this.matchesDrawn++;
    }
    public void increaseGoalsScored(int goals) {
        this.goalsScored += goals;
    }
    public void increaseGoalsConceded(int goals) {
        this.goalsConceded += goals;
    }
    public void updateOverallScore(int points) {
        this.overallScore += points;
    }

}