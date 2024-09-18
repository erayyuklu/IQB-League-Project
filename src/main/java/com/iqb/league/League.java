package com.iqb.league;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class League {
    private List<Team> teams;
    private List<List<Match>> firstHalfFixtures;
    private List<List<Match>> secondHalfFixtures;

    // Constructor with LeagueService injected
    public League(List<Team> teams, LeagueService leagueService) {
        List<Team> mutableTeams = new ArrayList<>(teams); // Copying to an ArrayList to make it mutable
        mutableTeams.sort((t1, t2) -> t1.getName().compareTo(t2.getName())); // Ordering the teams by name
        this.teams = mutableTeams;

        if (leagueService == null) {
            throw new IllegalArgumentException("LeagueService must not be null");
        }
        this.firstHalfFixtures = leagueService.generateFirstHalfFixtures(mutableTeams);
        this.secondHalfFixtures = leagueService.generateSecondHalfFixtures(this.firstHalfFixtures);
    }
}
