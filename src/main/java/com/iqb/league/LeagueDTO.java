package com.iqb.league;

import lombok.Data;
import java.util.List;

@Data
public class LeagueDTO {
    private List<String> teamNames;
    private List<MatchDTO> matches;

    // Constructor

    public LeagueDTO() {
    }

    public LeagueDTO(List<String> teamNames, List<MatchDTO> matches) {
        this.teamNames = teamNames;
        this.matches = matches;
    }
}