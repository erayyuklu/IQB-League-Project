package com.iqb.league;

import com.iqb.league.filter.AuthenticationFilter; // AuthenticationFilter sınıfını içe aktar
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import jakarta.servlet.Filter;

@SpringBootApplication
@ComponentScan(basePackages = {"com.iqb.league.controller", "com.iqb.league.service", "com.iqb.league.config"})
public class IqbLeagueProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(IqbLeagueProjectApplication.class, args);
    }

    @Bean
    public Filter authenticationFilter() {
        return new AuthenticationFilter(); // Filtreyi bean olarak kaydet
    }
}