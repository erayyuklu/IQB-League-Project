package com.iqb.league;

import com.iqb.league.dto.TeamDTO;
import com.iqb.league.model.Color;
import com.iqb.league.model.DetailedTeamPoints;
import com.iqb.league.model.Team;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.Converter;

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
                // Convert List<Color> to String[] for colors
                map().setColors(source.getColors() != null ?
                        source.getColors().stream()
                                .map(Color::getColorName)
                                .toArray(String[]::new) : null);
            }
        });

        // Custom mapping for TeamDTO to Team
        // MapperConfig class
        modelMapper.addMappings(new PropertyMap<TeamDTO, Team>() {
            @Override
            protected void configure() {
                map().setId(source.getId());
                map().setName(source.getName());
                map().setFoundationYear(source.getFoundationYear());
                // Convert String[] to List<Color> for colors
                using((Converter<String[], List<Color>>) context -> context.getSource() != null ?
                        Arrays.stream(context.getSource())
                                .map(Color::new)
                                .collect(Collectors.toList()) : null).map(source.getColors(), destination.getColors());


                using((Converter<Void, DetailedTeamPoints>) context -> {
                    return new DetailedTeamPoints();
                }).map(source, destination.getDetailedTeamPoints());
            }
        });

        return modelMapper;
    }
}
