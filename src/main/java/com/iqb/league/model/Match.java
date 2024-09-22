package com.iqb.league.model;

import lombok.Data;

@Data
public class Match {
    private Team homeTeam;
    private Team awayTeam;
    private byte homeScore;
    private byte awayScore;

    // Constructor
    public Match(Team homeTeam, Team awayTeam) {
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.homeScore = 0;
        this.awayScore = 0;
    }
}
