package com.iqb.league;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class League {
    private List<Team> teams;
    private List<List<Match>> firstHalfFixtures;
    private List<List<Match>> secondHalfFixtures;

    //Constructor
    public League(List<Team> teams) {
        List<Team> mutableTeams = new ArrayList<>(teams);//copying to an arraylist to make it mutable
        mutableTeams.sort((t1, t2) -> t1.getName().compareTo(t2.getName()));//ordering the teams by name
        this.teams = mutableTeams;
        this.firstHalfFixtures = generateFirstHalfFixtures(mutableTeams);
        this.secondHalfFixtures = generateSecondHalfFixtures(firstHalfFixtures);
    }



    // Method to generate fixtures based on the teams
    public List<List<Match>> generateFirstHalfFixtures(List<Team> teams) {
        int numberOfTeams = teams.size();
        int numberOfRounds = numberOfTeams - 1; // Toplam hafta sayısı
        int matchesPerRound = numberOfTeams / 2; // Her hafta oynanan maç sayısı
        List<List<Match>> rounds = new ArrayList<>(); // Fikstürü tutacak liste

        // Takımları dizelim, ilk takım sabit kalacak
        List<Team> rotatedTeams = new ArrayList<>(teams);

        // Takımların son oynadığı maçın ev-deplasman durumunu tutalım (true = ev sahibi, false = deplasman)
        Map<Team, Boolean> lastHomeAwayStatus = new HashMap<>();
        for (Team team : teams) {
            lastHomeAwayStatus.put(team, true); // İlk hafta her takım ev sahibi olabilir
        }

        // İlk hafta fikstürü
        for (int round = 0; round < numberOfRounds; round++) {
            List<Match> currentRound = new ArrayList<>();

            // Her hafta için maçları oluştur
            for (int matchIndex = 0; matchIndex < matchesPerRound; matchIndex++) {
                Team homeTeam = rotatedTeams.get(matchIndex);
                Team awayTeam = rotatedTeams.get(numberOfTeams - 1 - matchIndex);

                // Son hafta ev sahibi/deplasman olma durumuna göre takım rollerini değiştir
                if (!lastHomeAwayStatus.get(homeTeam)) {
                    // Eğer homeTeam önceki hafta deplasmandaysa, bu hafta ev sahibi olabilir
                    currentRound.add(new Match(homeTeam, awayTeam));
                    lastHomeAwayStatus.put(homeTeam, true);
                    lastHomeAwayStatus.put(awayTeam, false);
                } else if (!lastHomeAwayStatus.get(awayTeam)) {
                    // Eğer awayTeam önceki hafta deplasmandaysa, bu hafta ev sahibi olabilir
                    currentRound.add(new Match(awayTeam, homeTeam));
                    lastHomeAwayStatus.put(awayTeam, true);
                    lastHomeAwayStatus.put(homeTeam, false);
                } else {
                    // Eğer her iki takım da geçen hafta ev sahibi olmuşsa, random bir takımın ev sahibi olmasını sağlayabiliriz
                    currentRound.add(new Match(homeTeam, awayTeam)); // Varsayılan sıralama
                    lastHomeAwayStatus.put(homeTeam, true);
                    lastHomeAwayStatus.put(awayTeam, false);
                }
            }

            rounds.add(currentRound);

            // Her hafta sonunda takımları saat yönünde döndür (ilk takım hariç)
            Team lastTeam = rotatedTeams.remove(rotatedTeams.size() - 1);
            rotatedTeams.add(1, lastTeam);
        }

        return rounds;
    }

    public List<List<Match>> generateSecondHalfFixtures(List<List<Match>> firstHalfFixtures) {
        List<List<Match>> secondHalfFixtures = new ArrayList<>();

        // İlk yarıdaki fikstürleri döngü ile gezerek ikinci yarı fikstürünü oluştur
        for (List<Match> round : firstHalfFixtures) {
            List<Match> secondHalfRound = new ArrayList<>();
            for (Match match : round) {
                // Ev sahibi ve deplasman takımlarını tersine çevir
                secondHalfRound.add(new Match(match.getAwayTeam(), match.getHomeTeam()));
            }
            secondHalfFixtures.add(secondHalfRound);
        }

        return secondHalfFixtures;
    }





}