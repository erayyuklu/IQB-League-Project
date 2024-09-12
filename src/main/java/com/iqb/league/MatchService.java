package com.iqb.league;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class MatchService {

    // Method to process MatchDTO before the match simulation
    public void dto_process_before_match(Match match, boolean isFirst, byte weekNum, MatchDTO matchDTO) {
        // Fill in details before the match
        matchDTO.setMatchId(0); // Assuming ID is auto-generated by the DB
        matchDTO.setMatch_first_or_second(isFirst ? 'f' : 's');
        matchDTO.setMatch_week_num(weekNum);

        // Set home team details
        matchDTO.setHomeTeamName(match.getHomeTeam().getName());
        matchDTO.setHomeTeamId(match.getHomeTeam().getId());
        matchDTO.setHomeOverallBefore(match.getHomeTeam().getOverallScore());

        // Set away team details
        matchDTO.setAwayTeamName(match.getAwayTeam().getName());
        matchDTO.setAwayTeamId(match.getAwayTeam().getId());
        matchDTO.setAwayOverallBefore(match.getAwayTeam().getOverallScore());
    }

    // Method to process MatchDTO after the match simulation
    public void dto_process_after_match(Match match, MatchDTO matchDTO) {
        // Set the overall scores after the match
        matchDTO.setHomeOverallAfter(match.getHomeTeam().getOverallScore());
        matchDTO.setAwayOverallAfter(match.getAwayTeam().getOverallScore());

        // Set match scores
        matchDTO.setHomeScore(match.getHomeScore());
        matchDTO.setAwayScore(match.getAwayScore());
    }

    // New method to process all matches in the league
    public void do_matches(League league) {
        // Process first half matches
        List<List<Match>> firstHalfFixtures = league.getFirstHalfFixtures();
        List<MatchDTO> firstHalfDTOs =process_half_matches(firstHalfFixtures, true);
        saveMatchDTOsToDatabase(firstHalfDTOs);

        // Process second half matches
        List<List<Match>> secondHalfFixtures = league.getSecondHalfFixtures();
        List<MatchDTO> secondHalfDTOs = process_half_matches(secondHalfFixtures, false);
        saveMatchDTOsToDatabase(secondHalfDTOs);
    }

    // Helper method to process matches for each half
    private List<MatchDTO> process_half_matches(List<List<Match>> halfFixtures, boolean isFirst) {
        List<MatchDTO> matchDTOs = new ArrayList<>();
        for (byte weekNum = 1; weekNum <= halfFixtures.size(); weekNum++) {
            List<Match> weeklyMatches = halfFixtures.get(weekNum - 1); // Get matches for this week

            for (Match match : weeklyMatches) {
                MatchDTO matchDTO = new MatchDTO(); // Create a new DTO for each match

                // Process before match
                dto_process_before_match(match, isFirst, weekNum, matchDTO);

                // Simulate the match
                match.simulateMatch();

                // Process after match
                dto_process_after_match(match, matchDTO);

                matchDTOs.add(matchDTO);
            }
        }

        return matchDTOs;
    }

    private Connection connection;

    public MatchService(Connection connection) {
        this.connection = connection;
    }

    public void saveMatchDTOsToDatabase(List<MatchDTO> matchDTOs) {
        String insertSQL = "INSERT INTO matches (" +
                "match_first_or_second, " +
                "match_week_num, " +
                "match_home_team_name, " +
                "match_home_team_id, " +
                "match_away_team_name, " +
                "match_away_team_id, " +
                "match_home_overall_before, " +
                "match_away_overall_before, " +
                "match_home_overall_after, " +
                "match_away_overall_after, " +
                "match_home_score, " +
                "match_away_score" +
                ") VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(insertSQL)) {

            for (MatchDTO dto : matchDTOs) {
                pstmt.setString(1, String.valueOf(dto.getMatch_first_or_second()));
                pstmt.setShort(2, dto.getMatch_week_num());
                pstmt.setString(3, dto.getHomeTeamName());
                pstmt.setInt(4, dto.getHomeTeamId());
                pstmt.setString(5, dto.getAwayTeamName());
                pstmt.setInt(6, dto.getAwayTeamId());
                pstmt.setInt(7, dto.getHomeOverallBefore());
                pstmt.setInt(8, dto.getAwayOverallBefore());
                pstmt.setInt(9, dto.getHomeOverallAfter());
                pstmt.setInt(10, dto.getAwayOverallAfter());
                pstmt.setByte(11, dto.getHomeScore());
                pstmt.setByte(12, dto.getAwayScore());

                pstmt.addBatch();
            }

            // Execute the batch
            pstmt.executeBatch();

        } catch (SQLException e) {
            e.printStackTrace();
            // Handle SQL exceptions
        }
    }
}
