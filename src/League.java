import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class League {
    private List<Team> teams;
    private List<Match> fixtures;

    //Constructor
    public League(List<Team> teams) {
        List<Team> mutableTeams = new ArrayList<>(teams);//copying to an arraylist to make it mutable
        mutableTeams.sort((t1, t2) -> t1.getName().compareTo(t2.getName()));//ordering the teams by name
        this.teams = mutableTeams;
        this.fixtures = generateFixtures(mutableTeams);
    }



    // Method to generate fixtures based on the teams
    private List<Match> generateFixtures(List<Team> teams) {
        int numberOfTeams = teams.size();
        int numberOfMatches=numberOfTeams*(numberOfTeams-1)/2;
        int k=0;
        List<Match> matches = new ArrayList<>();
        List<List<Integer>> pairs = new ArrayList<>();

        while(k<numberOfMatches){
            int rand1 = (int)(Math.random() * numberOfTeams);
            int rand2 = (int)(Math.random() * numberOfTeams);
            if(rand1 != rand2 && !pairs.contains(List.of(rand1, rand2)) && !pairs.contains(List.of(rand2, rand1))){
                pairs.add(List.of(rand1,rand2));
                matches.add(new Match(teams.get(rand1),teams.get(rand2)));
                k++;
            }
        }
        return matches;
    }
}