package com.iqb.league;

import lombok.Data;

@Data
public class MatchDTO {
    private int matchId; // match_id
    private char match_first_or_second;
    private short match_week_num;
    private String homeTeamName; // match_home_team_name
    private int homeTeamId; // match_home_team_id
    private String awayTeamName; // match_away_team_name
    private int awayTeamId; // match_away_team_id
    private int homeOverallBefore; // match_home_overall_before
    private int awayOverallBefore; // match_away_overall_before
    private int homeOverallAfter; // match_home_overall_after
    private int awayOverallAfter; // match_away_overall_after
    private byte homeScore; // match_home_score
    private byte awayScore; // match_away_score

    // Constructors
    public MatchDTO() {
    }

    public MatchDTO(int matchId, char match_first_or_second, short match_week_num,String homeTeamName, int homeTeamId, String awayTeamName, int awayTeamId,
                    int homeOverallBefore, int awayOverallBefore, int homeOverallAfter, int awayOverallAfter,
                    byte homeScore, byte awayScore) {
        this.matchId = matchId;
        this.homeTeamName = homeTeamName;
        this.homeTeamId = homeTeamId;
        this.awayTeamName = awayTeamName;
        this.awayTeamId = awayTeamId;
        this.homeOverallBefore = homeOverallBefore;
        this.awayOverallBefore = awayOverallBefore;
        this.homeOverallAfter = homeOverallAfter;
        this.awayOverallAfter = awayOverallAfter;
        this.homeScore = homeScore;
        this.awayScore = awayScore;
    }
}
