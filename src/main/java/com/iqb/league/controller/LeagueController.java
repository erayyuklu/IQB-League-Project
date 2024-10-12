package com.iqb.league.controller;
import com.iqb.league.dto.DetailedTeamPointsDTO;
import com.iqb.league.model.Team;
import com.iqb.league.model.Color;
import com.iqb.league.service.LeagueService;
import com.iqb.league.dto.TeamDTO;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/{key}")
public class LeagueController {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private LeagueService leagueService;

    @GetMapping("/teams/show")
    public ResponseEntity<?> showTeams() {
        List<TeamDTO> teams = leagueService.showTeams(modelMapper);
        return ResponseEntity.ok(teams);
    }

    @PostMapping(value = "/teams/add", consumes = "application/json")
    public ResponseEntity<?> addTeam(@RequestBody TeamDTO teamDTO) {
        try {
            leagueService.addTeam(modelMapper, teamDTO);
            return ResponseEntity.ok("New team successfully added!");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error adding team: " + e.getMessage());
        }
    }


    @DeleteMapping(value = "/teams/delete", consumes = "application/json")
    public ResponseEntity<?> deleteTeam(@RequestBody Map<String, Integer> payload) {
        int teamId = payload.get("teamId");
        leagueService.deleteTeam(teamId);
        return ResponseEntity.ok("Team successfully deleted.");
    }


    @PostMapping(value="/teams/detailed_points", consumes = "application/json")
    public ResponseEntity<List<DetailedTeamPointsDTO>> getTeamPointsByLeagueId(@RequestBody Map<String, Integer> payload) {
        int leagueId = payload.get("league_id");
        List<DetailedTeamPointsDTO> points = leagueService.getTeamPointsByLeagueId(leagueId);

        if (points.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(points);
    }


    @PostMapping(value="/teams/special_statistics", consumes = "application/json")
    public ResponseEntity<Integer> getTeamStatistics(@RequestBody Map<String, Object> payload) {
        int leagueId = (int) payload.get("leagueId");
        int teamId = (int) payload.get("teamId");
        String statisticType = (String) payload.get("statisticType");

        Integer statisticValue = leagueService.getTeamStatistics(leagueId, teamId, statisticType);

        if (statisticValue == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(statisticValue);
    }


    @GetMapping("/fixtures")
    public ResponseEntity<String> getFixtures() {
        String fixtures = leagueService.createFixturesAPI();
        return ResponseEntity.ok(fixtures);
    }
    @PostMapping(value="/teams/colors", consumes = "application/json")
    public ResponseEntity<List<String>> getTeamColors(@RequestBody Map<String, Integer> payload) {
        Integer teamId = payload.get("teamId");

        Team team = leagueService.findTeamById(teamId);
        if (team == null) {
            return ResponseEntity.notFound().build();
        }

        List<String> colors = team.getColors().stream()
                .map(Color::getColorName)
                .toList();

        return ResponseEntity.ok(colors);
    }


    @PostMapping(value="/teams/foundation_year", consumes = "application/json")
    public ResponseEntity<Short> getFoundationYear(@RequestBody Map<String, Integer> payload) {
        Integer teamId = payload.get("teamId");

        Team team = leagueService.findTeamById(teamId);
        if (team == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(team.getFoundationYear());
    }


    @PostMapping(value="/start_league", consumes = "application/json")
    public ResponseEntity<String> startLeague(@RequestBody Map<String, String> payload) {
        String leagueName = payload.get("leagueName");


        try {
            String result = leagueService.startLeague(leagueName);
            return ResponseEntity.ok(result);
        } catch (SQLException e) {
            return ResponseEntity.status(500).body("Error starting league: " + e.getMessage());
        }
    }








}
