package com.iqb.league.service;

import com.iqb.league.dto.DetailedTeamPointsDTO;
import com.iqb.league.dto.LeagueDTO;
import com.iqb.league.model.Color;
import com.iqb.league.model.Match;
import com.iqb.league.model.Team;
import com.iqb.league.dto.ColorDTO;
import com.iqb.league.dto.TeamDTO;
import org.modelmapper.ModelMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.sql.Connection;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class LeagueService {

    private final Connection connection;

    @Autowired
    public LeagueService(Connection connection) {
        this.connection = connection;
    }
    public int saveLeagueDTOToDatabase(LeagueDTO leagueDTO) {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        int id = 0;

        try {
            String sql = "INSERT INTO leagues (league_name) VALUES (?) RETURNING league_id";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, leagueDTO.getLeagueName());


            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                int leagueId = resultSet.getInt("league_id");
                System.out.println("League ID: " + leagueId);
                id=leagueId;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (preparedStatement != null) preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return id;
    }


    // Method to generate fixtures based on the teams
    public List<List<Match>> generateFirstHalfFixtures(List<Team> teams) {
        int numberOfTeams = teams.size();
        int numberOfRounds = numberOfTeams - 1; // Total number of rounds
        int matchesPerRound = numberOfTeams / 2; // Number of matches per round
        List<List<Match>> rounds = new ArrayList<>(); // Fixture list

        // Rotate teams to create the fixture
        List<Team> rotatedTeams = new ArrayList<>(teams);

        // Track the home/away status of each team
        Map<Team, Boolean> lastHomeAwayStatus = new HashMap<>();
        for (Team team : teams) {
            lastHomeAwayStatus.put(team, true); // Initially, all teams can be home
        }

        // Generate fixtures for each round
        for (int round = 0; round < numberOfRounds; round++) {
            List<Match> currentRound = new ArrayList<>();

            for (int matchIndex = 0; matchIndex < matchesPerRound; matchIndex++) {
                Team homeTeam = rotatedTeams.get(matchIndex);
                Team awayTeam = rotatedTeams.get(numberOfTeams - 1 - matchIndex);

                // Determine home/away status for the current match
                if (!lastHomeAwayStatus.get(homeTeam)) {
                    currentRound.add(new Match(homeTeam, awayTeam));
                    lastHomeAwayStatus.put(homeTeam, true);
                    lastHomeAwayStatus.put(awayTeam, false);
                } else if (!lastHomeAwayStatus.get(awayTeam)) {
                    currentRound.add(new Match(awayTeam, homeTeam));
                    lastHomeAwayStatus.put(awayTeam, true);
                    lastHomeAwayStatus.put(homeTeam, false);
                } else {
                    currentRound.add(new Match(homeTeam, awayTeam)); // Default assignment
                    lastHomeAwayStatus.put(homeTeam, true);
                    lastHomeAwayStatus.put(awayTeam, false);
                }
            }

            rounds.add(currentRound);

            // Rotate teams for the next round (excluding the first team)
            Team lastTeam = rotatedTeams.remove(rotatedTeams.size() - 1);
            rotatedTeams.add(1, lastTeam);
        }

        return rounds;
    }

    public List<List<Match>> generateSecondHalfFixtures(List<List<Match>> firstHalfFixtures) {
        List<List<Match>> secondHalfFixtures = new ArrayList<>();

        // Create second half fixtures by reversing the home/away teams of the first half fixtures
        for (List<Match> round : firstHalfFixtures) {
            List<Match> secondHalfRound = new ArrayList<>();
            for (Match match : round) {
                secondHalfRound.add(new Match(match.getAwayTeam(), match.getHomeTeam()));
            }
            secondHalfFixtures.add(secondHalfRound);
        }

        return secondHalfFixtures;
    }

    public List<TeamDTO> showTeams(ModelMapper modelMapper) {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        List<TeamDTO> teams = new ArrayList<>();

        try {
            String sql = "SELECT t.id, t.name, t.foundation_year,  " +
                    "array_agg(c.color_name) AS colors " +
                    "FROM public.teams t " +
                    "LEFT JOIN team_colors tc ON t.id = tc.team_id " +
                    "LEFT JOIN colors c ON tc.color_id = c.id " +
                    "GROUP BY t.id " +
                    "ORDER BY t.id ASC";

            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();

            System.out.println("ID | Team Name | Year of Foundation | Colors");
            System.out.println("-------------------------------------------------------------");


            while (resultSet.next()) {
                TeamDTO teamDTO = new TeamDTO();
                teamDTO.setId(resultSet.getInt("id"));
                teamDTO.setName(resultSet.getString("name"));
                teamDTO.setFoundationYear(resultSet.getShort("foundation_year"));


                Array colorArray = resultSet.getArray("colors");
                if (colorArray != null) {
                    String[] colors = (String[]) colorArray.getArray();
                    teamDTO.setColors(colors);
                }
                teams.add(teamDTO);
                System.out.println(teamDTO.getId() + " | " + teamDTO.getName() + " | " + teamDTO.getFoundationYear() + " | " + String.join(", ", teamDTO.getColors()));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (preparedStatement != null) preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return teams;
    }

    public void addTeam(ModelMapper modelMapper, TeamDTO teamDTO) {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            // Add colors first and get their IDs
            List<Integer> colorIds = new ArrayList<>();
            for (String color : teamDTO.getColors()) {
                int colorId = addColor(color, modelMapper);
                colorIds.add(colorId);
            }

            // Insert the team
            String sql = "INSERT INTO public.teams (name, foundation_year) VALUES (?, ?) RETURNING id";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, teamDTO.getName());
            preparedStatement.setShort(2, teamDTO.getFoundationYear());

            resultSet = preparedStatement.executeQuery();
            int teamId = 0;
            if (resultSet.next()) {
                teamId = resultSet.getInt("id");
            }

            // Link colors to the team
            String linkSql = "INSERT INTO team_colors (team_id, color_id) VALUES (?, ?)";
            PreparedStatement linkStatement = connection.prepareStatement(linkSql);
            for (int colorId : colorIds) {
                linkStatement.setInt(1, teamId);
                linkStatement.setInt(2, colorId);
                linkStatement.executeUpdate();
            }

            System.out.println("New team successfully added!");

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (preparedStatement != null) preparedStatement.close();
                if (resultSet != null) resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void deleteTeam(int teamId) {
        PreparedStatement preparedStatement = null;

        try {
            // Delete the team's colors first
            String deleteColorsSql = "DELETE FROM team_colors WHERE team_id = ?";
            preparedStatement = connection.prepareStatement(deleteColorsSql);
            preparedStatement.setInt(1, teamId);
            preparedStatement.executeUpdate();

            // Delete the team
            String deleteTeamSql = "DELETE FROM public.teams WHERE id = ?";
            preparedStatement = connection.prepareStatement(deleteTeamSql);
            preparedStatement.setInt(1, teamId);
            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows > 0) {
                System.out.println("Team successfully deleted.");
            } else {
                System.out.println("No team found with the specified ID.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (preparedStatement != null) preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public int addColor(String colorName, ModelMapper modelMapper) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        ColorDTO colorDTO = new ColorDTO();
        Color colorEntity = new Color(); // Your entity class

        try {
            // Check if the color already exists in the database
            String query = "SELECT id, color_name FROM colors WHERE color_name = ?";
            ps = connection.prepareStatement(query);
            ps.setString(1, colorName);
            rs = ps.executeQuery();

            if (rs.next()) {
                // If the color exists, create a ColorDTO from the result
                colorDTO.setId(rs.getInt("id"));
                colorDTO.setColorName(rs.getString("color_name"));
            } else {
                // If the color does not exist, map ColorDTO to Color entity
                colorDTO.setColorName(colorName);
                modelMapper.map(colorDTO, colorEntity); // Automapping to entity

                // Insert the color and get the ID
                String insert = "INSERT INTO colors (color_name) VALUES (?) RETURNING id";
                ps = connection.prepareStatement(insert);
                ps.setString(1, colorDTO.getColorName());
                rs = ps.executeQuery();

                if (rs.next()) {
                    colorDTO.setId(rs.getInt("id"));
                    // Optionally, you can map back to DTO if needed
                    modelMapper.map(colorEntity, colorDTO);
                }
            }
        } finally {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
        }
        return colorDTO.getId();
    }






}
