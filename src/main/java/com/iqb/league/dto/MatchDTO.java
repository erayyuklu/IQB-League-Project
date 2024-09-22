package com.iqb.league.dto;

import lombok.Data;

@Data
public class MatchDTO {
    private int matchId;
    private char match_first_or_second;
    private short match_week_num;
    private int homeTeamId;
    private int awayTeamId;
    private int homeOverallBefore;
    private int awayOverallBefore;
    private int homeOverallAfter;
    private int awayOverallAfter;
    private byte homeScore;
    private byte awayScore;
    private int leagueId;
    // Constructors
    public MatchDTO() {
    }
}
