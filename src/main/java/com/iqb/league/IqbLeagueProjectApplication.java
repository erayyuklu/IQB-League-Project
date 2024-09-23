package com.iqb.league;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.iqb.league.controller", "com.iqb.league.service", "com.iqb.league.config"})
public class IqbLeagueProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(IqbLeagueProjectApplication.class, args);
    }
}