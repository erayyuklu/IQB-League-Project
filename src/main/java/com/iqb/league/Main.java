package com.iqb.league;

import org.modelmapper.ModelMapper;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static final String RESET = "\u001B[0m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";
    public static final String PURPLE = "\u001B[35m";
    public static final String HIGHLINE = "\u001B[4m";

    private Connection connection;
    private LeagueService leagueService;
    private ModelMapper modelMapper;

    public Main(Connection connection, LeagueService leagueService, ModelMapper modelMapper) {
        this.connection = connection;
        this.leagueService = leagueService;
        this.modelMapper = modelMapper;
    }

    public static void main(String[] args) {
        Connection connection = null;
        try {
            // Create a connection to the database
            connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/mydatabase", "postgres", "12345");

            // Create instances of services and ModelMapper
            ModelMapper modelMapper = MapperConfig.createModelMapper();
            LeagueService leagueService = new LeagueService(connection);
            Main mainApp = new Main(connection, leagueService, modelMapper);

            Scanner scanner = new Scanner(System.in);
            int choice = 0;

            while (choice != -1) {
                System.out.println("Choose an option:" + PURPLE + HIGHLINE);
                System.out.println("(1) Show teams in database" + GREEN);
                System.out.println("(2) Add a team into database" + RED);
                System.out.println("(3) Delete a team from database" + YELLOW);
                System.out.println("(4) Create fixture with teams in database" + BLUE);
                System.out.println("(5) Start the league with current teams" + RESET);
                System.out.println("Enter your choice (or -1 to exit):" + RESET);

                choice = scanner.nextInt();

                switch (choice) {
                    case 1:
                        mainApp.showTeams();
                        break;
                    case 2:
                        mainApp.addTeam();
                        break;
                    case 3:
                        mainApp.deleteTeam();
                        break;
                    case 4:
                        mainApp.createFixtures();
                        break;
                    case 5:
                        mainApp.startLeague();
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

    private void showTeams() {
        leagueService.showTeams(modelMapper);
    }

    private void addTeam() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter the name of the team:");
        String name = scanner.nextLine();

        System.out.println("Enter the foundation year of the team:");
        short foundationYear = scanner.nextShort();
        scanner.nextLine(); // Consume newline

        System.out.println("Enter the colors of the team (comma-separated):");
        String colorInput = scanner.nextLine();
        String[] colors = colorInput.split("\\s*,\\s*");

        leagueService.addTeam(modelMapper, new TeamDTO(name, foundationYear, colors));
    }

    private void deleteTeam() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter the ID of the team you want to delete:");
        int teamId = scanner.nextInt();
        leagueService.deleteTeam(teamId);
    }

    private void createFixtures() {
        TeamService teamService = new TeamService();
        List<TeamDTO> teamDTOs = teamService.takeTeams();
        List<Team> teams = teamService.convertToTeams(teamDTOs);

        // Create a league using the list of teams and the LeagueService instance
        League league = new League(teams, leagueService);

        // Get fixtures from the league
        List<List<Match>> firstHalfFixtures = league.getFirstHalfFixtures();
        List<List<Match>> secondHalfFixtures = league.getSecondHalfFixtures();

        // Display fixtures
        System.out.println("First half fixtures:");
        for (int i = 0; i < firstHalfFixtures.size(); i++) {
            System.out.println("Week " + (i + 1) + ":");
            for (Match match : firstHalfFixtures.get(i)) {
                System.out.println(match.getHomeTeam().getName() + " vs. " + match.getAwayTeam().getName());
            }
            System.out.println();
        }

        System.out.println("Second half fixtures:");
        for (int i = 0; i < secondHalfFixtures.size(); i++) {
            System.out.println("Week " + (i + 1) + ":");
            for (Match match : secondHalfFixtures.get(i)) {
                System.out.println(match.getHomeTeam().getName() + " vs. " + match.getAwayTeam().getName());
            }
            System.out.println();
        }
    }

    private void startLeague() {
        TeamService teamService = new TeamService();
        List<TeamDTO> teamDTOs = teamService.takeTeams();
        List<Team> teams = teamService.convertToTeams(teamDTOs);

        // Create a league using the list of teams and the LeagueService instance
        League league = new League(teams, leagueService);

        MatchService matchService = new MatchService(connection);
        matchService.do_matches(league);
    }
}
