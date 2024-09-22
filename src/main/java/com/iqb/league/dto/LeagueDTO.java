package com.iqb.league.dto;

import lombok.Data;

@Data
public class LeagueDTO {
    private int leagueId;       // Lig kimliği (PRIMARY KEY)
    private String leagueName;  // Lig adı (NOT NULL)

    // Constructors
    public LeagueDTO() {
    }
    public LeagueDTO(String leagueName) {
        this.leagueName = leagueName;
    }
}
