package com.iqb.league.service;

import com.iqb.league.model.League;
import com.iqb.league.model.Match;
import com.iqb.league.dto.MatchDTO;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;

@Service
public class MatchService {
    private final Connection connection;

    public MatchService(Connection connection) {
        this.connection = connection;
    }


    // Method to simulate a match and determine the winner
    public String simulateMatch(Match match) {

        // Initialize a StringBuilder to accumulate the result message
        StringBuilder resultBuilder = new StringBuilder();

        // Base chances
        double homeBaseChance = 0.50;
        double awayBaseChance = 0.50;

        // Home advantage effect
        double homeAdvantage = 0.10; // %10
        double homeChanceWithAdvantage = homeBaseChance + homeAdvantage;

        // OverallScore effect
        double scoreDifference = match.getHomeTeam().getDetailedTeamPoints().getOverallScore() - match.getAwayTeam().getDetailedTeamPoints().getOverallScore();
        double scaledEffect;

        if (scoreDifference < -100) {
            scaledEffect = -0.25; // Difference is less than -100
        } else if (scoreDifference > 100) {
            scaledEffect = +0.25;  // Difference is more than +100
        } else {
            scaledEffect = scoreDifference * 25 / 10000; // Difference is between -100 and +100
        }

        // Update the home and away chances
        double homeChance = Math.min(homeChanceWithAdvantage + scaledEffect, 1.0);
        double awayChance = 1.0 - homeChance;

        // Draw chance
        double drawChance = 0.10;

        // Normalize the possibilities
        double totalChance = homeChance + awayChance;
        homeChance = (homeChance / totalChance) * (1.0 - drawChance);
        awayChance = (awayChance / totalChance) * (1.0 - drawChance);

        // Determine the winner
        double random = Math.random();
        if (random <= drawChance) {
            byte score = (byte) (Math.random() * 3);
            match.setHomeScore(score); // Scores between 0-2
            match.getHomeTeam().getDetailedTeamPoints().increaseGoalsScored(score);
            match.getHomeTeam().getDetailedTeamPoints().increaseMatchesDrawn();
            match.getAwayTeam().getDetailedTeamPoints().increaseGoalsConceded(score);

            match.setAwayScore(match.getHomeScore()); // Same score
            match.getAwayTeam().getDetailedTeamPoints().increaseGoalsScored(match.getAwayScore());
            match.getAwayTeam().getDetailedTeamPoints().increaseMatchesDrawn();
            match.getHomeTeam().getDetailedTeamPoints().increaseGoalsConceded(score);

            // Append the draw result to StringBuilder and console output
            String drawResult = match.getHomeTeam().getName() + " (home) - " + match.getAwayTeam().getName() + " (away) The match ended in a draw with a score of " + match.getHomeScore() + "-" + match.getAwayScore() + ".";
            resultBuilder.append(drawResult).append("\n");
        } else if (random <= drawChance + homeChance) {
            byte homeScore = (byte) (Math.random() * 3 + 2); // Scores between 2-4
            byte awayScore = (byte) (Math.random() * 2); // Scores between 0-1
            match.setHomeScore(homeScore);
            match.getHomeTeam().getDetailedTeamPoints().increaseGoalsScored(homeScore);
            match.getHomeTeam().getDetailedTeamPoints().increaseMatchesWon();
            match.getAwayTeam().getDetailedTeamPoints().increaseGoalsConceded(homeScore);

            match.setAwayScore(awayScore);
            match.getAwayTeam().getDetailedTeamPoints().increaseGoalsScored(awayScore);
            match.getAwayTeam().getDetailedTeamPoints().increaseMatchesLost();
            match.getHomeTeam().getDetailedTeamPoints().increaseGoalsConceded(awayScore);

            // Append the home win result to StringBuilder and console output
            String homeWinResult = match.getHomeTeam().getName() + " (home) won the match against " + match.getAwayTeam().getName() + " (away) with a score of " + match.getHomeScore() + "-" + match.getAwayScore() + "!";
            resultBuilder.append(homeWinResult).append("\n");
        } else {
            byte homeScore = (byte) (Math.random() * 2); // Scores between 0-1
            byte awayScore = (byte) (Math.random() * 3 + 2); // Scores between 2-4
            match.setHomeScore(homeScore);
            match.getHomeTeam().getDetailedTeamPoints().increaseGoalsScored(homeScore);
            match.getHomeTeam().getDetailedTeamPoints().increaseMatchesLost();
            match.getAwayTeam().getDetailedTeamPoints().increaseGoalsConceded(homeScore);

            match.setAwayScore(awayScore);
            match.getAwayTeam().getDetailedTeamPoints().increaseGoalsScored(awayScore);
            match.getAwayTeam().getDetailedTeamPoints().increaseMatchesWon();
            match.getHomeTeam().getDetailedTeamPoints().increaseGoalsConceded(awayScore);

            // Append the away win result to StringBuilder and console output
            String awayWinResult = match.getAwayTeam().getName() + " (away) won the match against " + match.getHomeTeam().getName() + " (home) with a score of " + match.getAwayScore() + "-" + match.getHomeScore() + "!";
            resultBuilder.append(awayWinResult).append("\n");
        }

        // After simulating, update the overall scores and goal differences
        updateOverallScores(match);
        match.getHomeTeam().getDetailedTeamPoints().updateGoalDifference();
        match.getAwayTeam().getDetailedTeamPoints().updateGoalDifference();

        // Return the accumulated result as a string
        return resultBuilder.toString();
    }

    // Method to update the overall scores of the teams after the match
    public void updateOverallScores(Match match) {
        if (match.getHomeScore() > match.getAwayScore()) {
            match.getHomeTeam().getDetailedTeamPoints().updateOverallScore(3);
            match.getAwayTeam().getDetailedTeamPoints().updateOverallScore(-1);
        } else if (match.getAwayScore() > match.getHomeScore()) {
            match.getAwayTeam().getDetailedTeamPoints().updateOverallScore(3);
            match.getHomeTeam().getDetailedTeamPoints().updateOverallScore(-1);
        } else {
            match.getHomeTeam().getDetailedTeamPoints().updateOverallScore(1);
            match.getAwayTeam().getDetailedTeamPoints().updateOverallScore(1);
        }
    }

    // Method to process MatchDTO before the match simulation
    public void dto_process_before_match(Match match, boolean isFirst, byte weekNum, MatchDTO matchDTO, int leagueId) {
        // Fill in details before the match
        matchDTO.setMatchId(0); // Assuming ID is auto-generated by the DB
        matchDTO.setMatch_first_or_second(isFirst ? 'f' : 's');
        matchDTO.setMatch_week_num(weekNum);

        // Set home team details
        matchDTO.setHomeTeamId(match.getHomeTeam().getId());
        matchDTO.setHomeOverallBefore(match.getHomeTeam().getDetailedTeamPoints().getOverallScore());

        // Set away team details
        matchDTO.setAwayTeamId(match.getAwayTeam().getId());
        matchDTO.setAwayOverallBefore(match.getAwayTeam().getDetailedTeamPoints().getOverallScore());

        matchDTO.setLeagueId(leagueId);
    }

    // Method to process MatchDTO after the match simulation
    public void dto_process_after_match(Match match, MatchDTO matchDTO) {
        // Set the overall scores after the match
        matchDTO.setHomeOverallAfter(match.getHomeTeam().getDetailedTeamPoints().getOverallScore());
        matchDTO.setAwayOverallAfter(match.getAwayTeam().getDetailedTeamPoints().getOverallScore());

        // Set match scores
        matchDTO.setHomeScore(match.getHomeScore());
        matchDTO.setAwayScore(match.getAwayScore());
    }

    // New method to process all matches in the league
    public String do_matches(League league) {
        // Initialize a StringBuilder to accumulate match summaries
        StringBuilder matchSummaries = new StringBuilder();
        // Process first half matches
        matchSummaries.append("First half fixtures:\n");
        List<List<Match>> firstHalfFixtures = league.getFirstHalfFixtures();
        List<MatchDTO> firstHalfDTOs =process_half_matches(firstHalfFixtures, true, getLeagueIdByName(league.getLeagueName()), matchSummaries);
        saveMatchDTOsToDatabase(firstHalfDTOs);

        // Process second half matches
        matchSummaries.append("\nSecond half fixtures:\n");
        List<List<Match>> secondHalfFixtures = league.getSecondHalfFixtures();
        List<MatchDTO> secondHalfDTOs = process_half_matches(secondHalfFixtures, false, getLeagueIdByName(league.getLeagueName()), matchSummaries);
        saveMatchDTOsToDatabase(secondHalfDTOs);
        return matchSummaries.toString();
    }

    // Helper method to process matches for each half
    private List<MatchDTO> process_half_matches(List<List<Match>> halfFixtures, boolean isFirst, int leagueId, StringBuilder matchSummaries) {
        List<MatchDTO> matchDTOs = new ArrayList<>();

        for (byte weekNum = 1; weekNum <= halfFixtures.size(); weekNum++) {
            matchSummaries.append("Week ").append(weekNum).append(":\n");
            List<Match> weeklyMatches = halfFixtures.get(weekNum - 1); // Get matches for this week

            for (Match match : weeklyMatches) {
                MatchDTO matchDTO = new MatchDTO(); // Create a new DTO for each match

                // Process before match
                dto_process_before_match(match, isFirst, weekNum, matchDTO,leagueId);

                // Simulate the match
                String MatchString=simulateMatch(match);  // Call MatchService's simulateMatch
                matchSummaries.append(MatchString);

                // Process after match
                dto_process_after_match(match, matchDTO);

                matchDTOs.add(matchDTO);
            }
        }

        return matchDTOs;
    }


    public void saveMatchDTOsToDatabase(List<MatchDTO> matchDTOs) {
        String insertSQL = "INSERT INTO matches (" +
                "match_first_or_second, " +
                "match_week_num, " +
                "match_home_team_id, " +
                "match_away_team_id, " +
                "match_home_overall_before, " +
                "match_away_overall_before, " +
                "match_home_overall_after, " +
                "match_away_overall_after, " +
                "match_home_score, " +
                "match_away_score," +
                "league_id"+
                ") VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?)";

        try (PreparedStatement pstmt = connection.prepareStatement(insertSQL)) {

            for (MatchDTO dto : matchDTOs) {
                pstmt.setString(1, String.valueOf(dto.getMatch_first_or_second()));
                pstmt.setShort(2, dto.getMatch_week_num());
                pstmt.setInt(3, dto.getHomeTeamId());
                pstmt.setInt(4, dto.getAwayTeamId());
                pstmt.setInt(5, dto.getHomeOverallBefore());
                pstmt.setInt(6, dto.getAwayOverallBefore());
                pstmt.setInt(7, dto.getHomeOverallAfter());
                pstmt.setInt(8, dto.getAwayOverallAfter());
                pstmt.setByte(9, dto.getHomeScore());
                pstmt.setByte(10, dto.getAwayScore());
                pstmt.setInt(11, dto.getLeagueId());

                pstmt.addBatch();
            }

            // Execute the batch
            pstmt.executeBatch();

        } catch (SQLException e) {
            e.printStackTrace();
            // Handle SQL exceptions
        }
    }

    public Integer getLeagueIdByName(String leagueName) {
        String query = "SELECT league_id FROM leagues WHERE league_name = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, leagueName);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("league_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
