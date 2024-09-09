package com.iqb.league;

import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;

import java.util.List;
import java.util.stream.Collectors;

public class MapperConfig {

    public static ModelMapper createModelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        // Custom mapping for Team to TeamDTO
        modelMapper.addMappings(new PropertyMap<Team, TeamDTO>() {
            @Override
            protected void configure() {
                map().setId(source.getId());
                map().setName(source.getName());
                map().setFoundationYear(source.getFoundationYear());
                map().setOverallScore(source.getOverallScore());

                // Convert List<Color> to List<String> for colors
                if (source.getColors() != null) {
                    map().setColors(source.getColors().stream()
                            .map(Color::getColorName)
                            .toArray(String[]::new));
                } else {
                    map().setColors(null);
                }
            }
        });

        return modelMapper;
    }
}
