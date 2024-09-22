package com.iqb.league.controller;


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

}
