package com.iqb.league;

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

    // Method to simulate a match and determine the winner
    public void simulateMatch() {
        // Base chances
        double homeBaseChance = 0.50;
        double awayBaseChance = 0.50;

        // home advantage effect
        double homeAdvantage = 0.10; // %10
        double homeChanceWithAdvantage = homeBaseChance + homeAdvantage;

        // OverallScore effect
        double scoreDifference = homeTeam.getOverallScore() - awayTeam.getOverallScore();
        double scaledEffect;

        if (scoreDifference < -100) {
            scaledEffect = -0.25; // difference is less than -100
        } else if (scoreDifference > 100) {
            scaledEffect = +0.25;  // difference is more than +100
        } else {
            scaledEffect = scoreDifference * 25 / 10000; // difference is between -100 and +100
        }

        // update the home and away chances
        double homeChance = Math.min(homeChanceWithAdvantage + scaledEffect, 1.0);
        double awayChance = 1.0 - homeChance;

        // Draw chance
        double drawChance = 0.10;

        // Normalize the possibilities
        double totalChance = homeChance + awayChance;
        homeChance = (homeChance / totalChance) * (1.0 - drawChance);
        awayChance = (awayChance / totalChance) * (1.0 - drawChance);

        // determine the winner
        double random = Math.random();
        if (random <= drawChance) {
            homeScore = (byte)(Math.random() * 3); // scores between 0-2
            awayScore = homeScore; // same score
            System.out.println(homeTeam.getName() + " (home) - " + awayTeam.getName() + " (away) The match ended in a draw with a score of " + homeScore + "-" + awayScore + ".");
        } else if (random <= drawChance + homeChance) {
            homeScore = (byte)(Math.random() * 3 + 2); // scores between 2-4
            awayScore = (byte)(Math.random() * 2); // scores between 0-1
            System.out.println(homeTeam.getName() + " (home) won the match against " + awayTeam.getName() + " (away) with a score of " + homeScore + "-" + awayScore + "!");
        } else {
            homeScore = (byte)(Math.random() * 2); // scores between 0-1
            awayScore = (byte)(Math.random() * 3 + 2); // scores between 2-4
            System.out.println(awayTeam.getName() + " (away) won the match against " + homeTeam.getName() + " (home) with a score of " + awayScore + "-" + homeScore + "!");
        }

        updateOverallScores();
    }

    // Method to update the overall scores of the teams after the match
    private void updateOverallScores() {
        // if home team won
        if (homeScore > awayScore) {
            homeTeam.setOverallScore(homeTeam.getOverallScore() + 3);
            awayTeam.setOverallScore(awayTeam.getOverallScore() - 1);
        }
        // if away team won
        else if (awayScore > homeScore) {
            awayTeam.setOverallScore(awayTeam.getOverallScore() + 3);
            homeTeam.setOverallScore(homeTeam.getOverallScore() - 1);
        }
        // if the match ended in a draw
        else {
            homeTeam.setOverallScore(homeTeam.getOverallScore() + 1);
            awayTeam.setOverallScore(awayTeam.getOverallScore() + 1);
        }
    }

}