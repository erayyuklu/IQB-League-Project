package com.iqb.league.controller;


import com.iqb.league.Main;
import com.iqb.league.dto.DetailedTeamPointsDTO;
import com.iqb.league.model.Team;
import com.iqb.league.model.Color;
import com.iqb.league.service.LeagueService;
import com.iqb.league.dto.TeamDTO;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;

import java.util.List;


@RestController
@RequestMapping("/teams")
public class LeagueController {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private LeagueService leagueService;

    @GetMapping("/show")
    public ResponseEntity<?> showTeams() {
        List<TeamDTO> teams = leagueService.showTeams(modelMapper);
        return ResponseEntity.ok(teams);
    }

    @PostMapping("/add/{name}/{foundationYear}/{colors}")
    public ResponseEntity<?> addTeam(
            @PathVariable String name,
            @PathVariable short foundationYear,
            @PathVariable String colors) {

        String[] colorArray = colors.split("\\s*,\\s*");

        TeamDTO teamDTO = new TeamDTO(name, foundationYear, colorArray);

        try {
            leagueService.addTeam(modelMapper, teamDTO);
            return ResponseEntity.ok("New team successfully added!");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error adding team: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete/{teamId}")
    public ResponseEntity<?> deleteTeam(@PathVariable int teamId) {
        leagueService.deleteTeam(teamId);
        return ResponseEntity.ok("Team successfully deleted.");
    }

    @GetMapping("/detailed_points/{leagueName}")
    public ResponseEntity<List<DetailedTeamPointsDTO>> getTeamPointsByLeagueName(@PathVariable String leagueName) {
        List<DetailedTeamPointsDTO> points = leagueService.getTeamPointsByLeagueName(leagueName);
        if (points.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(points);
    }

    @GetMapping("/detailed_points/{leagueName}/{teamName}/{statisticType}")
    public ResponseEntity<Integer> getTeamStatistics(@PathVariable String leagueName, @PathVariable String teamName, @PathVariable String statisticType) {
        Integer statisticValue = leagueService.getTeamStatistics(leagueName, teamName, statisticType);

        if (statisticValue == null) {
            return ResponseEntity.notFound().build(); // İstatistik bulunamazsa 404 döner
        }
        return ResponseEntity.ok(statisticValue);
    }

    @GetMapping("/fixtures")
    public ResponseEntity<String> getFixtures() {
        String fixtures = leagueService.createFixturesAPI();
        return ResponseEntity.ok(fixtures);
    }
    @GetMapping("/{teamName}/colors")
    public ResponseEntity<List<String>> getTeamColors(@PathVariable String teamName) {
        Team team = leagueService.findTeamByName(teamName); // Takımı bulmak için service çağrısı
        if (team == null) {
            return ResponseEntity.notFound().build(); // Takım bulunamazsa 404 döner
        }
        List<String> colors = team.getColors().stream()
                .map(Color::getColorName) // Renk isimlerini al
                .toList();
        return ResponseEntity.ok(colors);
    }

    @GetMapping("/{teamName}/foundation_year")
    public ResponseEntity<Short> getFoundationYear(@PathVariable String teamName) {
        Team team = leagueService.findTeamByName(teamName); // Takımı bulmak için service çağrısı
        if (team == null) {
            return ResponseEntity.notFound().build(); // Takım bulunamazsa 404 döner
        }
        return ResponseEntity.ok(team.getFoundationYear()); // Kuruluş yılını döndür
    }







}
