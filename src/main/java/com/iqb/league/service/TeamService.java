package com.iqb.league.service;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.iqb.league.model.DetailedTeamPoints;
import com.iqb.league.dto.DetailedTeamPointsDTO;
import com.iqb.league.model.Color;
import com.iqb.league.MapperConfig;
import com.iqb.league.model.Team;
import com.iqb.league.dto.TeamDTO;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TeamService {

    private ModelMapper modelMapper;

    private final Connection connection;

    // Constructor: take modelmappper from MapperConfig and connection from Spring
    @Autowired
    public TeamService(Connection connection) {
        this.connection = connection;
        this.modelMapper = MapperConfig.createModelMapper();
    }




    // Helper method that takes colors from PostgreSQL as String array and converts them to Color objects
    private List<Color> getColors(int teamId, Connection connection) throws SQLException {
        List<Color> colors = new ArrayList<>();
        String sqlColors = "SELECT c.color_name FROM team_colors tc "
                + "JOIN colors c ON tc.color_id = c.id WHERE tc.team_id = ?";
        try (PreparedStatement pstmtColors = connection.prepareStatement(sqlColors)) {
            pstmtColors.setInt(1, teamId);
            try (ResultSet rsColors = pstmtColors.executeQuery()) {
                while (rsColors.next()) {
                    colors.add(new Color(rsColors.getString("color_name")));
                    //print the color
                    System.out.println(rsColors.getString("color_name"));
                }
            }
        }
        return colors;
    }


    // methods that convert Teams to TeamDTOs
    public List<Team> convertToTeams(List<TeamDTO> teamDTOs) {
        return teamDTOs.stream()
                .map(dto -> modelMapper.map(dto, Team.class))
                .collect(Collectors.toList());
    }

    // Method to convert team list to DetailedTeamPointsDTO list
    public List<DetailedTeamPointsDTO> convertToDetailedTeamPointsDTOs(List<Team> teams) {
        return teams.stream()
                .map(team -> {
                    DetailedTeamPoints detailedTeamPoints = team.getDetailedTeamPoints();
                    return modelMapper.map(detailedTeamPoints, DetailedTeamPointsDTO.class);
                })
                .filter(Objects::nonNull) // filter the null objects
                .collect(Collectors.toList());
    }


    // Take teams from database and return List<TeamDTO>
    public List<TeamDTO> takeTeams() {
        List<TeamDTO> teamDTOs = new ArrayList<>();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            // Connect to the database
            connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/mydatabase", "postgres", "12345");

            // SQL query to get team information along with colors
            String sql = "SELECT t.id, t.name, t.foundation_year,  "
                    + "(SELECT array_agg(c.color_name) FROM team_colors tc JOIN colors c ON tc.color_id = c.id WHERE tc.team_id = t.id) AS colors "
                    + "FROM teams t";
            preparedStatement = connection.prepareStatement(sql);

            // Execute the query
            resultSet = preparedStatement.executeQuery();

            // Process the result set
            while (resultSet.next()) {
                // Retrieve values from the result set
                int teamId = resultSet.getInt("id");
                String name = resultSet.getString("name");
                short foundationYear = resultSet.getShort("foundation_year");


                // Retrieve and process the colors
                Array colorArray = resultSet.getArray("colors");
                String[] colorNames = (colorArray != null) ? (String[]) colorArray.getArray() : new String[0];

                // Create a TeamDTO object
                TeamDTO teamDTO = new TeamDTO(name, foundationYear, colorNames);
                teamDTO.setId(teamId);


                // Add the TeamDTO object to the list
                teamDTOs.add(teamDTO);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (preparedStatement != null) preparedStatement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return teamDTOs;
    }

    public void saveDetailedTeamPointsDTOsToDatabase(List<DetailedTeamPointsDTO> detailedTeamPointsDTOS) throws SQLException {
        Connection connection;
        connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/mydatabase", "postgres", "12345");


        String sql = "INSERT INTO detailed_team_points (team_id, goals_scored, goals_conceded, " +
                "matches_won, matches_lost, matches_drawn, goal_difference, overall_score, league_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            for (DetailedTeamPointsDTO dto : detailedTeamPointsDTOS) {
                preparedStatement.setInt(1, dto.getTeamId());
                preparedStatement.setInt(2, dto.getGoalsScored());
                preparedStatement.setInt(3, dto.getGoalsConceded());
                preparedStatement.setInt(4, dto.getMatchesWon());
                preparedStatement.setInt(5, dto.getMatchesLost());
                preparedStatement.setInt(6, dto.getMatchesDrawn());
                preparedStatement.setInt(7, dto.getGoalDifference());
                preparedStatement.setInt(8, dto.getOverallScore());
                preparedStatement.setInt(9, dto.getLeagueId());
                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch(); // make all additions as a whole at once
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void resetDetailedTeamPoints(List<Team> teams) {
        for (Team team : teams) {
            team.getDetailedTeamPoints().setGoalsScored(0);
            team.getDetailedTeamPoints().setGoalsConceded(0);
            team.getDetailedTeamPoints().setMatchesWon(0);
            team.getDetailedTeamPoints().setMatchesLost(0);
            team.getDetailedTeamPoints().setMatchesDrawn(0);
            team.getDetailedTeamPoints().setGoalDifference(0);
            team.getDetailedTeamPoints().setOverallScore(0);
        }
    }

    public void IdMappingForTeamAndPoints(Team team, int leagueId) {
        team.getDetailedTeamPoints().setTeamId(team.getId());// Update detailedTeamPoints teamId
        team.getDetailedTeamPoints().setLeagueId(leagueId); // Update detailedTeamPoints leagueId
    }

}
