import org.modelmapper.ModelMapper;
import java.util.List;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        ModelMapper modelMapper = MapperConfig.createModelMapper();

        // create teams for testing
        Team Galatasaray = new Team("Galatasaray", (short) 1905, new String[]{"red", "blue"});
        Team Fenerbahce = new Team("Fenerbahce", (short) 1907, new String[]{"yellow", "blue"});
        Team Besiktas = new Team("Besiktas", (short) 1903, new String[]{"black", "white"});
        Team Trabzonspor = new Team("Trabzonspor", (short) 1967, new String[]{"blue", "claret"});
        Team Bursaspor = new Team("Bursaspor", (short) 1963, new String[]{"green", "white"});
        Team Sivasspor = new Team("Sivasspor", (short) 1967, new String[]{"red", "white"});
        Team Antalyaspor = new Team("Antalyaspor", (short) 1966, new String[]{"red", "white"});
        Team Alanyaspor = new Team("Alanyaspor", (short) 1948, new String[]{"orange", "green"});

        // create league
        League firstLeague = new League(List.of(Galatasaray, Fenerbahce, Besiktas, Trabzonspor, Bursaspor, Sivasspor, Antalyaspor, Alanyaspor));

        // create the dtos first
        List<TeamDTO> teamDTOs = firstLeague.getTeams().stream()
                .map(team -> modelMapper.map(team, TeamDTO.class))
                .collect(Collectors.toList());

        //print the teamDTOs line by line
        teamDTOs.forEach(System.out::println);


        List<MatchDTO> matchDTOs = firstLeague.getFixtures().stream()
                .map(match -> modelMapper.map(match, MatchDTO.class))
                .collect(Collectors.toList());

        LeagueDTO leagueDTO = new LeagueDTO(
                teamDTOs.stream().map(TeamDTO::getName).collect(Collectors.toList()),
                matchDTOs
        );
        System.out.println("First League DTO (before matches): " + leagueDTO);

        // Stimulate the matches
        for (int i = 0; i < firstLeague.getFixtures().size(); i++) {
            firstLeague.getFixtures().get(i).simulateMatch();
        }

        // create updated matchdtos
        List<MatchDTO> updatedMatchDTOs = firstLeague.getFixtures().stream()
                .map(match -> modelMapper.map(match, MatchDTO.class))
                .collect(Collectors.toList());

        leagueDTO.setMatches(updatedMatchDTOs);

        // print the updated leagueDTO
        System.out.println("Updated League DTO: " + leagueDTO);
    }
}
