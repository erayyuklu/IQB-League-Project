package com.iqb.league;

import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

import java.util.Arrays;
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
                // Convert List<Color> to String[] for colors
                map().setColors(source.getColors() != null ?
                        source.getColors().stream()
                                .map(Color::getColorName)
                                .toArray(String[]::new) : null);
            }
        });

        // Custom mapping for TeamDTO to Team
        modelMapper.addMappings(new PropertyMap<TeamDTO, Team>() {
            @Override
            protected void configure() {
                map().setId(source.getId());
                map().setName(source.getName());
                map().setFoundationYear(source.getFoundationYear());
                map().setOverallScore(source.getOverallScore());
                // Convert String[] to List<Color> for colors
                using(new Converter<String[], List<Color>>() {
                    @Override
                    public List<Color> convert(MappingContext<String[], List<Color>> context) {
                        return context.getSource() != null ?
                                Arrays.stream(context.getSource())
                                        .map(Color::new)
                                        .collect(Collectors.toList()) : null;
                    }
                }).map(source.getColors(), destination.getColors());
            }
        });

        return modelMapper;
    }
}
