package com.iqb.league;

import lombok.Data;

import java.util.List;

@Data
public class Team {
    private int id;
    private String name;
    private short foundationYear;
    private List<Color> colors; // Use Color array for consistent representation
    private int overallScore;

    // Constructor
    public Team(String name, short foundationYear, List<Color>  colors) {
        this.name = name;
        this.foundationYear = foundationYear;
        this.colors = colors;
        this.overallScore = 0;
    }
}
