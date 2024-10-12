package com.iqb.league.dto;

import lombok.Data;

@Data
public class LeagueDTO {
    private int leagueId;
    private String leagueName;

    // Constructors
    public LeagueDTO() {
    }
    public LeagueDTO(String leagueName) {
        this.leagueName = leagueName;
    }
}
