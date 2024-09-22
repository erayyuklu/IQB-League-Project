package com.iqb.league.model;

import lombok.Data;

import java.util.List;

@Data
public class Team {
    private int id;
    private String name;
    private short foundationYear;
    private List<Color> colors; // Use Color array for consistent representation
    private DetailedTeamPoints detailedTeamPoints;

    // Constructor
    public Team(String name, short foundationYear, List<Color>  colors) {
        this.name = name;
        this.foundationYear = foundationYear;
        this.colors = colors;
        this.detailedTeamPoints = new DetailedTeamPoints();
    }

    public Team() {
        this.detailedTeamPoints = new DetailedTeamPoints();
    }


}
