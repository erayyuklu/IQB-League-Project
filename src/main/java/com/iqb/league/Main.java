package com.iqb.league;

import org.modelmapper.ModelMapper;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    private Connection connection;

    public Main(Connection connection) {
        this.connection = connection;
    }

    public static void main(String[] args) {
        Connection connection = null;
        try {
            // Create a connection to the database
            connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/mydatabase", "postgres", "12345");
            // Create an instance of the Main class with the connection
            Main mainApp = new Main(connection);
            ModelMapper modelMapper = MapperConfig.createModelMapper();
            Scanner scanner = new Scanner(System.in);
            int choice = 0;

            while (choice != -1) {
                System.out.println("Choose an option:");
                System.out.println("(1) Show teams in database");
                System.out.println("(2) Add/delete teams");
                System.out.println("(3) Create fixture with teams in database");
                System.out.println("(4) Start the league with current teams");
                System.out.println("Enter your choice (or -1 to exit):");

                choice = scanner.nextInt();

                switch (choice) {
                    case 1:
                        mainApp.showTeams(modelMapper); // Call non-static method on instance
                        break;
                    case 2:
                        Scanner scanner2 = new Scanner(System.in);
                        System.out.println("Enter the name of the team:");
                        String name = scanner2.nextLine(); // first input for team name

                        System.out.println("Enter the foundation year of the team:");
                        short foundationYear = scanner2.nextShort(); // second input for foundation year


                        Scanner scanner3 = new Scanner(System.in);
                        System.out.println("Enter the colors of the team (comma-separated):");
                        String colorInput = scanner3.nextLine(); // third input for colors
                        String[] colors = colorInput.split("\\s*,\\s*"); // Split by comma and trim whitespace

                        mainApp.addTeam(modelMapper, new TeamDTO(name, foundationYear, colors));
                        break;
                    case 3:
                        // Implement case 3
                        break;
                    case 4:
                        // Implement case 4
                        break;
                    case -1:
                        System.out.println("Exiting...");
                        break;
                    default:
                        System.out.println("Invalid option. Please try again.");
                }
            }
            scanner.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Close the connection in the finally block
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void showTeams(ModelMapper modelMapper) {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            String sql = "SELECT t.id, t.name, t.foundation_year, t.overall_score, " +
                    "array_agg(c.color_name) AS colors " +
                    "FROM public.teams t " +
                    "LEFT JOIN team_colors tc ON t.id = tc.team_id " +
                    "LEFT JOIN colors c ON tc.color_id = c.id " +
                    "GROUP BY t.id " +
                    "ORDER BY t.id ASC";

            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();

            System.out.println("ID | Team Name | Year of Foundation | Overall Score | Colors");
            System.out.println("-------------------------------------------------------------");

            while (resultSet.next()) {
                TeamDTO teamDTO = new TeamDTO();
                teamDTO.setId(resultSet.getInt("id"));
                teamDTO.setName(resultSet.getString("name"));
                teamDTO.setFoundationYear(resultSet.getShort("foundation_year"));
                teamDTO.setOverallScore(resultSet.getInt("overall_score"));

                Array colorArray = resultSet.getArray("colors");
                if (colorArray != null) {
                    String[] colors = (String[]) colorArray.getArray();
                    teamDTO.setColors(colors);
                }

                System.out.println(teamDTO.getId() + " | " + teamDTO.getName() + " | " + teamDTO.getFoundationYear() + " | " + teamDTO.getOverallScore() + " | " + String.join(", ", teamDTO.getColors()));
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
    }

    public void addTeam(ModelMapper modelMapper, TeamDTO teamDTO) {
        PreparedStatement preparedStatement = null;
        Connection connection = null;

        try {
            // Get a valid connection from your connection pool or DriverManager
            connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/mydatabase", "postgres", "12345");

            // Add colors first and get their IDs
            List<Integer> colorIds = new ArrayList<>();
            for (String color : teamDTO.getColors()) {
                int colorId = addColor(color);
                colorIds.add(colorId);
            }

            // Insert the team
            String sql = "INSERT INTO public.teams (name, foundation_year, overall_score) VALUES (?, ?, ?) RETURNING id";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, teamDTO.getName());
            preparedStatement.setShort(2, teamDTO.getFoundationYear());
            preparedStatement.setInt(3, teamDTO.getOverallScore());

            // Execute the insertion and get the team ID
            ResultSet rs = preparedStatement.executeQuery();
            int teamId = 0;
            if (rs.next()) {
                teamId = rs.getInt("id");
            }

            // Link colors to the team
            String linkSql = "INSERT INTO team_colors (team_id, color_id) VALUES (?, ?)";
            PreparedStatement linkStatement = connection.prepareStatement(linkSql);
            for (int colorId : colorIds) {
                linkStatement.setInt(1, teamId);
                linkStatement.setInt(2, colorId);
                linkStatement.executeUpdate();
            }

            System.out.println("Yeni bir takım başarıyla eklendi!");

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (preparedStatement != null) preparedStatement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    public int addColor(String colorName) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        int colorId = 0;

        try {
            // Check if the color already exists in the database
            String query = "SELECT id FROM colors WHERE color_name = ?";
            ps = connection.prepareStatement(query);
            ps.setString(1, colorName);
            rs = ps.executeQuery();

            if (rs.next()) {
                // Return the ID if the color already exists
                colorId = rs.getInt("id");
            } else {
                // Insert the new color if it does not exist
                query = "INSERT INTO colors (color_name) VALUES (?) RETURNING id";
                ps = connection.prepareStatement(query);
                ps.setString(1, colorName);
                rs = ps.executeQuery();
                if (rs.next()) {
                    colorId = rs.getInt("id");
                } else {
                    throw new SQLException("Failed to insert new color: " + colorName);
                }
            }
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return colorId;
    }
}
