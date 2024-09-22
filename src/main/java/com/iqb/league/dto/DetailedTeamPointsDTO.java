package com.iqb.league.dto;

import lombok.Data;

@Data
public class DetailedTeamPointsDTO {
    private int teamId;
    private int goalsScored;
    private int goalsConceded;
    private int matchesWon;
    private int matchesLost;
    private int matchesDrawn;
    private int goalDifference;
    private int overallScore;
    private int leagueId;


    // No-argument constructor
    public DetailedTeamPointsDTO() {
    }


    // Constructor
    public DetailedTeamPointsDTO(int teamId, int goalsScored, int goalsConceded,
                                 int matchesWon, int matchesLost, int matchesDrawn,
                                 int goalDifference, int overallScore) {
        this.teamId = teamId;
        this.goalsScored = goalsScored;
        this.goalsConceded = goalsConceded;
        this.matchesWon = matchesWon;
        this.matchesLost = matchesLost;
        this.matchesDrawn = matchesDrawn;
        this.goalDifference = goalDifference;
        this.overallScore = overallScore;
    }
}
