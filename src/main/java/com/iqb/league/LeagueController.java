/*package com.iqb.league;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/leagues")
public class LeagueController {

    private final LeagueService leagueService;

    public LeagueController(LeagueService leagueService) {
        this.leagueService = leagueService;
    }

    // Tüm ligleri getiren örnek bir endpoint
    @GetMapping
    public List<LeagueDTO> getAllLeagues() {
        return leagueService.getAllLeagues();
    }

    // Başka API endpoint'leri ekleyebilirsiniz.
}
