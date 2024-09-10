package com.iqb.league;

import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.modelmapper.ModelMapper;

public class TeamService {

    private ModelMapper modelMapper;

    // Renkleri PostgreSQL'den String array olarak alıp Color nesnelerine dönüştüren yardımcı metot
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

    // Constructor: ModelMapper'ı MapperConfig'den al
    public TeamService() {
        this.modelMapper = MapperConfig.createModelMapper();
    }

    // TeamDTO'ları Team nesnelerine çeviren metot
    public List<Team> convertToTeams(List<TeamDTO> teamDTOs) {
        return teamDTOs.stream()
                .map(dto -> modelMapper.map(dto, Team.class)) // TeamDTO'dan Team'e dönüşüm
                .collect(Collectors.toList());
    }

    // Takımları veritabanından alıp List<TeamDTO> olarak döndür
    public List<TeamDTO> takeTeams() {
        List<TeamDTO> teamDTOs = new ArrayList<>();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            // Connect to the database
            connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/mydatabase", "postgres", "12345");

            // SQL query to get team information along with colors
            String sql = "SELECT t.id, t.name, t.foundation_year, t.overall_score, "
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
                int overallScore = resultSet.getInt("overall_score");

                // Retrieve and process the colors
                Array colorArray = resultSet.getArray("colors");
                String[] colorNames = (colorArray != null) ? (String[]) colorArray.getArray() : new String[0];

                // Create a TeamDTO object
                TeamDTO teamDTO = new TeamDTO(name, foundationYear, colorNames);
                teamDTO.setId(teamId);
                teamDTO.setOverallScore(overallScore);

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


}
