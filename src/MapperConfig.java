import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;

public class MapperConfig {
    public static ModelMapper createModelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        // custom match from Match to MatchDTO
        modelMapper.addMappings(new PropertyMap<Match, MatchDTO>() {
            @Override
            protected void configure() {
                map().setHomeTeamName(source.getHomeTeam().getName());
                map().setAwayTeamName(source.getAwayTeam().getName());
                map().setHomeScore(source.getHomeScore());
                map().setAwayScore(source.getAwayScore());
            }
        });

        return modelMapper;
    }
}
