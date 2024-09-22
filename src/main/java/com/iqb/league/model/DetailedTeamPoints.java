package com.iqb.league.model;

import lombok.Data;

@Data
public class DetailedTeamPoints {
    private int teamId;           // Takım ID'si
    private int goalsScored;      // Atılan gol sayısı
    private int goalsConceded;    // Yenilen gol sayısı
    private int matchesWon;       // Kazanılan maç sayısı
    private int matchesLost;      // Kaybedilen maç sayısı
    private int matchesDrawn;     // Berabere kalınan maç sayısı
    private int goalDifference;   // Gol averajı
    private int overallScore;    // Toplam puan
    private int leagueId;         // Lig ID'si

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